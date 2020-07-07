package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class PluginServiceImpl extends PluginService {

    private final ConfigurationSection config;

    private final Logger logger;
    private final File pluginFolder;
    private final PluginYml pluginYml;

    public PluginServiceImpl(final ConfigurationSection pluginYml){
        this.config = pluginYml;
        this.pluginYml = new PluginYmlImpl(pluginYml);
        this.pluginFolder = new File("/plugins/" + this.pluginYml.getPluginName());
        this.logger = Logger.getLogger(this.pluginYml.getPluginName());
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    public File getPluginFolder(){
        return pluginFolder;
    }

    @Override
    public PluginYml getPluginYml(){
        return pluginYml;
    }

    @Override
    public InputStream getResource(final String path){
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public ConfigurationFile createConfiguration(){
        return null; // todo: conf impl
    }

}
