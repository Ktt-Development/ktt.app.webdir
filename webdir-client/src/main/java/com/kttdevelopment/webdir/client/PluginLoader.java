package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
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
    private final List<WebDirPlugin> plugins = new ArrayList<>();

    public final List<PluginRendererEntry> getRenderers(){
        return Collections.unmodifiableList(renderers);
    }

    public final List<WebDirPlugin> getPlugins(){
        return Collections.unmodifiableList(plugins);
    }

    public final WebDirPlugin getPlugin(final String pluginName){
        for(final WebDirPlugin plugin : plugins){
            // todo
        }
        return null;
    }

    @SuppressWarnings("unused") // class param required for casting. It is NOT unused.
    public final <T extends WebDirPlugin> WebDirPlugin getPlugin(final String pluginName, final Class<T> pluginClass){
        return getPlugin(pluginName);
    }

    private final File pluginsFolder;
    private final boolean safe;

    public PluginLoader(final File pluginsFolder){
        final LocaleService locale = Main.getLocale();
        final YamlMapping config   = Main.getConfig();
        final Logger logger        = Main.getLogger(locale.getString("plugin-loader.name"));

        // todo: log init

        this.pluginsFolder = Objects.requireNonNull(pluginsFolder);

        if(!pluginsFolder.isDirectory())
            ; // todo: log ERR

        if(safe = Boolean.parseBoolean(config.string(ConfigService.SAFE))){
            // todo: log safe
            return;
        }

        // jar filter
        // todo: log
        final Map<File,URL> jars = new JarFilter().filter(pluginsFolder);
        final int init = jars.size();

        // plugin.yml filter + validate
        // todo: log
        final Map<File,YamlMapping> ymls = new YmlFilter().filter(jars);

        // validate + sort dep
        // todo: log
        final Map<File,YamlMapping> deps = new DependencyFilter().filter(ymls);

        // enable +verify dependents
        // todo: log
        final Map<WebDirPlugin,YamlMapping> loaded = null;
        // todo: log fin >> init - loaded.size()

        // save renderers
        loaded.forEach((plugin, yml) -> {
            final String pluginName = yml.string(NAME);
            plugin.getRenderers().forEach((name, renderer) -> {
                renderers.add(new PluginRendererEntry(pluginName, name, renderer));
                // todo: log add
            });
        });

        // todo: log finish
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
