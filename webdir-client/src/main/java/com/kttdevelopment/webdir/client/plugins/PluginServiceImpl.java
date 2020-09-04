package com.kttdevelopment.webdir.client.plugins;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.config.ConfigurationFile;
import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public final class PluginServiceImpl extends PluginService {

    private static final String badFileChars = "[\\\\/:*?\"<>|]";

    //

    private final Logger logger;
    private final File pluginFolder;
    private final PluginYml pluginYml;

    private final File sources, output, defaults, plugins;

    @SuppressWarnings("ConstantExpression")
    public PluginServiceImpl(final PluginYml pluginYml, final File pluginFolder){
        this.pluginYml      = pluginYml;
        this.pluginFolder   = new File(pluginFolder,pluginYml.getPluginName().replaceAll('[' + badFileChars + ']',"_"));
        this.logger         = Main.getLoggerService().getLogger(pluginYml.getPluginName());
        this.sources        = Main.getPageRenderingService().getSources();
        this.output         = Main.getPageRenderingService().getOutput();
        this.defaults       = Main.getPageRenderingService().getDefaults();
        this.plugins        = Main.getPluginLoader().getPluginFolder();
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
        return ExceptionUtil.requireNonExceptionElse(
            () -> {
                configurationSection.load(file);
                return configurationSection;
            },
            null
        );
    }

    @Override
    public final ConfigurationSection createConfiguration(final InputStream stream){
        final ConfigurationFile configurationSection = new ConfigurationFile();
        return ExceptionUtil.requireNonExceptionElse(
            () -> {
                configurationSection.load(stream);
                return configurationSection;
            },
            null
        );
    }

    @Override
    public LocaleBundle getLocaleBundle(final String resource){
        return new LocaleBundleImpl(Main.getLocaleService(), resource);
    }

    @Override
    public final File getSourcesFolder(){
        return sources;
    }

    @Override
    public final File getOutputFolder(){
        return output;
    }

    @Override
    public final File getDefaultsFolder(){
        return defaults;
    }

    @Override
    public final File getPluginsFolder(){
        return plugins;
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
        return new ToStringBuilder("PluginService")
            .addObject("logger",logger.getName())
            .addObject("badFileCharsRegex",badFileChars)
            .addObject("pluginFolder",pluginFolder.getAbsolutePath())
            .addObject("pluginYML",pluginYml)
            .toString();
    }

}
