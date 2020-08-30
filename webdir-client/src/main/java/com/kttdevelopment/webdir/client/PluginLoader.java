package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.plugins.PluginRendererEntry;
import com.kttdevelopment.webdir.client.plugins.filter.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class PluginLoader {

    private final List<PluginRendererEntry> renderers = new ArrayList<>();

    public final List<PluginRendererEntry> getRenderers(){
        return Collections.unmodifiableList(renderers);
    }

    private final List<WebDirPlugin> plugins = new ArrayList<>();

    public final List<WebDirPlugin> getPlugins(){
        return Collections.unmodifiableList(plugins);
    }

    //

    public final WebDirPlugin getPlugin(final String pluginName){
        for(final WebDirPlugin plugin : plugins)
            if(plugin.getPluginYml().getPluginName().equals(pluginName))
                return plugin;
        return null;
    }

    @SuppressWarnings({"unchecked", "unused", "RedundantSuppression"})
    // pluginClass parameter is REQUIRED for casting
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return (T) getPlugin(pluginName);
    }

    //

    private final File pluginFolder;
    private final boolean safe;

    PluginLoader(final File pluginFolder){
        final LocaleService locale          = Main.getLocaleService();
        final ConfigurationSection config   = Main.getConfigService().getConfig();
        final Logger logger                 = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const.started"));

        this.pluginFolder = pluginFolder;

        if(safe = config.getBoolean("safe")){
            logger.info(locale.getString("pluginLoader.const.safe"));
            return;
        }

        // jar filter
        logger.fine(locale.getString("pluginLoader.const.filter.jar"));
        final Map<File,URL> pluginJars = new JarFilter().filter(pluginFolder);

        final int init = pluginJars.size();

        // plugin.yml filter
        logger.fine(locale.getString("pluginLoader.const.filter.yml"));
        final Map<File,URL> pluginYMLs = new PluginYMLFilter().filter(pluginJars);

        // validate plugin.yml
        logger.fine(locale.getString("pluginLoader.const.filter.validYML"));
        final Map<File,PluginYml> validYMLs = new ValidPluginYMLFilter().filter(pluginYMLs);

        // validate dep
        logger.fine(locale.getString("pluginLoader.const.filter.validDep"));
        @SuppressWarnings("SpellCheckingInspection")
        final Map<File,PluginYml> validDeps = new DependencyFilter().filter(validYMLs);

        // sort dep
        logger.fine(locale.getString("pluginLoader.const.filter.sortDep"));
        @SuppressWarnings("SpellCheckingInspection")
        final Map<File,PluginYml> sortedDeps = new DependencySorter().filter(validDeps);

        // enable & check #main // +additional check to make sure dependency actually loaded
        logger.fine(locale.getString("pluginLoader.const.filter.main"));
        final List<WebDirPlugin> loaded = new PluginInitializer(pluginFolder).filter(sortedDeps);

        logger.info(locale.getString("pluginLoader.const.loaded",loaded.size(),init));

        // save renderers to here
        for(final WebDirPlugin webDirPlugin : loaded){
            final String pluginName = webDirPlugin.getPluginYml().getPluginName();
            webDirPlugin.getRenderers().forEach((name,renderer) -> {
                renderers.add(new PluginRendererEntry(pluginName,name,renderer));
                logger.info(locale.getString("pluginLoader.const.addRender",name,pluginName));
            });
        }

        logger.info(locale.getString("pluginLoader.const.finished"));
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginsFolder",pluginFolder)
            .addObject("safe",safe)
            .addObject("plugins",plugins)
            .addObject("renderers",renderers)
            .toString();
    }

}
