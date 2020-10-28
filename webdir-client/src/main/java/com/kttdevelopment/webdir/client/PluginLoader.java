package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.client.plugin.PluginRendererEntry;
import com.kttdevelopment.webdir.client.plugin.filter.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class PluginLoader {

    public static final String
        MAIN         = "main",
        NAME         = "name",
        DEPENDENCIES = "dependencies";

    private final List<PluginRendererEntry> renderers = new ArrayList<>();
    private final Map<String,WebDirPlugin> plugins = new HashMap<>();

    public final List<PluginRendererEntry> getRenderers(){
        return Collections.unmodifiableList(renderers);
    }

    public final List<WebDirPlugin> getPlugins(){
        return new ArrayList<>(plugins.values());
    }

    public final WebDirPlugin getPlugin(final String pluginName){
        for(final Map.Entry<String, WebDirPlugin> entry : plugins.entrySet())
            if(entry.getKey().equals(pluginName))
                return entry.getValue();
        return null;
    }

    @SuppressWarnings({"unused", "unchecked"}) // class param required for casting. It is NOT unused.
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return (T) getPlugin(pluginName);
    }

    private final File pluginsFolder;
    private final boolean safe;

    public PluginLoader(final File pluginsFolder){
        final LocaleService locale = Main.getLocale();
        final YamlMapping config   = Main.getConfig();
        final Logger logger        = Main.getLogger(locale.getString("plugin-loader.name"));

        logger.info(locale.getString("plugin-loader.constructor.start"));

        this.pluginsFolder = Objects.requireNonNull(pluginsFolder);

        if(pluginsFolder.exists() && !pluginsFolder.isDirectory())
            logger.severe(locale.getString("plugin-loader.constructor.dir", pluginsFolder.getPath()));

        if(safe = Boolean.parseBoolean(config.string(ConfigService.SAFE))){
            logger.info(locale.getString("plugin-loader.constructor.safe"));
            return;
        }

        // jar filter
        logger.fine(locale.getString("plugin-loader.filter.jar.start"));
        final Map<File,URL> jars = new JarFilter().filter(pluginsFolder);
        final int init = jars.size();

        // plugin.yml filter + validate
        logger.fine(locale.getString("plugin-loader.filter.yml.start"));
        final Map<File,YamlMapping> ymls = new YmlFilter().filter(jars);

        // validate + sort dep
        logger.fine(locale.getString("plugin-loader.filter.dep.start"));
        final Map<File,YamlMapping> deps = new DependencyFilter().filter(ymls);

        // enable +verify dependents
        logger.fine(locale.getString("plugin-loader.filter.enable.start"));
        final Map<YamlMapping,WebDirPlugin> loaded = new PluginInitializer().filter(deps);
        logger.fine(locale.getString("plugin-loader.filter.jar.start", loaded.size(), init));

        // save renderers
        loaded.forEach((yml, plugin) -> {
            final String pluginName = yml.string(NAME);
            plugins.put(pluginName, plugin);
            plugin.getRenderers().forEach((name, renderer) -> {
                renderers.add(new PluginRendererEntry(pluginName, name, renderer));
                logger.finer(locale.getString("plugin-loader.constructor.renderer", name, pluginName));
            });
        });

        logger.info(locale.getString("plugin-loader.constructor.finish"));
    }

    public final File getPluginsFolder(){
        return pluginsFolder;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginFolder", pluginsFolder)
            .addObject("safe", safe)
            .addObject("plugins", plugins)
            .addObject("renderers", renderers)
            .toString();
    }

}
