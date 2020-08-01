package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class PluginServiceImpl extends PluginService {

    private static final String badFileChars = "[\\\\/:*?\"<>|]";

    //

    private final Logger logger;
    private final File pluginFolder;
    private final PluginYml pluginYml;

    public PluginServiceImpl(final ConfigurationSection pluginYml, final File pluginFolder){
        this.pluginYml = new PluginYmlImpl(pluginYml);
        this.pluginFolder = Paths.get( pluginFolder.getPath() ,this.pluginYml.getPluginName().replaceAll('[' + badFileChars + ']',"_")).toFile();
        this.logger = Main.getLoggerService().getLogger(this.pluginYml.getPluginName());
    }

    @Override
    public final Logger getLogger(){
        return logger;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public final File getPluginFolder(){
        if(!pluginFolder.exists()) pluginFolder.mkdirs();
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
    public final ConfigurationSection createConfiguration(final File file){
        final ConfigurationFile configurationSection = new ConfigurationFile();
        try{
            configurationSection.load(file);
            return configurationSection;
        }catch(final Throwable ignored){
            return null;
        }
    }

    @Override
    public final ConfigurationSection createConfiguration(final InputStream stream){
        final ConfigurationFile configurationSection = new ConfigurationFile();
        try{
            configurationSection.load(stream);
            return configurationSection;
        }catch(final Throwable ignored){
            return null;
        }
    }

    @Override
    public LocaleBundle getLocaleBundle(final String resource){
        return new LocaleBundleImpl(Main.getLocaleService(),resource);
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof PluginService))
            return false;
        final PluginService other = ((PluginService) o);
        return other.getLogger().getName().equals(logger.getName()) &&
            other.getPluginFolder().getAbsolutePath().equals(pluginFolder.getAbsolutePath()) &&
            other.getPluginYml().equals(pluginYml);
    }

    @Override
    public String toString(){
        return new toStringBuilder("PluginService")
            .addObject("logger",logger.getName())
            .addObject("badFileCharsRegex",badFileChars)
            .addObject("pluginFolder",pluginFolder.getAbsolutePath())
            .addObject("pluginYML",pluginYml)
            .toString();
    }

}
