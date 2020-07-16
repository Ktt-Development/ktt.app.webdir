package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.*;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.config.SafeConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

public final class PluginServiceImpl extends PluginService {

    private final Logger logger;
    private final File pluginFolder;
    private final PluginYml pluginYml;

    public PluginServiceImpl(final ConfigurationSection pluginYml, final File pluginFolder){
        this.pluginYml = new PluginYmlImpl(pluginYml);
        this.pluginFolder = Paths.get( pluginFolder.getPath() ,this.pluginYml.getPluginName()).toFile();
        this.logger = Main.getLoggerService().getLogger(this.pluginYml.getPluginName());
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
    public WebDirPlugin getPlugin(final String pluginName){
        return Main.getPluginLoader().getPlugin(pluginName);
    }

    @Override
    public <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return Main.getPluginLoader().getPlugin(pluginName,pluginClass);
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
    public final ConfigurationFile createConfiguration(final InputStream stream){
        return new SafeConfigurationFileImpl(stream);
    }

    @Override
    public LocaleBundle getLocaleBundle(final String resource){
        return new LocaleBundleImpl(Main.getLocaleService(),resource);
    }

}
