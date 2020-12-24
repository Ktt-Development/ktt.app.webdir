package com.kttdevelopment.webdir.client.plugin;

import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PluginServiceImpl extends PluginService {

    private static final Pattern badFileChars = Pattern.compile("[\\\\/:*?\"<>|]");

    private final String pluginName;
    private final Logger logger;
    private final Map<String,Object> yml, cfg;
    private final File pluginFolder, sources, output, defaults, plugins;

    @SuppressWarnings("unchecked")
    public PluginServiceImpl(final Map<String,Object> plugin, final File pluginFolder){
        this.pluginName     = plugin.get(PluginLoader.NAME).toString();
        this.logger         = Main.getLogger(pluginName);
        this.pluginFolder   = new File(pluginFolder,badFileChars.matcher(pluginName).replaceAll("_"));
        this.yml            = MapUtility.asStringObjectMap(plugin);
        this.cfg            = MapUtility.deepCopy(MapUtility.asStringObjectMap(Main.getConfig()));
        this.sources        = new File(Main.getConfig().get(ConfigService.SOURCES).toString());
        this.output         = new File(Main.getConfig().get(ConfigService.OUTPUT).toString());
        this.defaults       = new File(Main.getConfig().get(ConfigService.DEFAULT).toString());
        this.plugins        = new File(Main.getConfig().get(ConfigService.PLUGINS).toString());
    }

    @Override
    public final String getPluginName(){
        return pluginName;
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
    public final Map<String,? super Object> getPluginYml(){
        return yml;
    }

    @Override
    public final Map<String,? super Object> getConfigYml(){
        return cfg;
    }

    @Override
    public final WebDirPlugin getPlugin(final String pluginName){
        return Main.getPluginLoader().getPlugin(pluginName);
    }

    @Override
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return Main.getPluginLoader().getPlugin(pluginName, pluginClass);
    }

    @Override
    public final LocaleBundle getLocaleBundle(final String resource, final ClassLoader classLoader){
        return new LocaleBundleImpl(Main.getLocale(), resource, classLoader);
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

    @Override
    public String toString(){
        return new ToStringBuilder("PluginService")
            .addObject("logger", logger)
            .addObject("pluginFolder", pluginFolder)
            .addObject("sources", sources)
            .addObject("output", output)
            .addObject("defaults", defaults)
            .addObject("plugins", plugins)
            .toString();
    }

}
