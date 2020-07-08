package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.*;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.config.SafeConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class PluginServiceImpl extends PluginService {

    private final Logger logger;
    private final File pluginFolder;
    private final PluginYml pluginYml;

    public PluginServiceImpl(final ConfigurationSection pluginYml){
        this.pluginYml = new PluginYmlImpl(pluginYml);
        this.pluginFolder = new File("/plugins/" + this.pluginYml.getPluginName());
        this.logger = Logger.getLogger(this.pluginYml.getPluginName());
    }

    @Override
    public final Logger getLogger(){
        return logger;
    }

    @Override
    public final File getPluginFolder(){
        return pluginFolder;
    }

    @Override
    public final PluginYml getPluginYml(){
        return pluginYml;
    }

    @Override
    public final InputStream getResource(final String path){
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public final ConfigurationFile createConfiguration(final File file){
        return new SafeConfigurationFileImpl(file);
    }

    @Override
    public LocaleBundle getLocaleBundle(final String resource){
        return new LocaleBundleImpl(Main.getLocaleService(),resource);
    }

}
