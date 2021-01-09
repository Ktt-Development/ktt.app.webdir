/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client;

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
        return plugins;
    }

    public final WebDirPlugin getPlugin(final String pluginName){
        for(final WebDirPlugin plugin : plugins)
            if(plugin.getPluginName().equals(pluginName))
                return plugin;
        return null;
    }

    @SuppressWarnings({"unused", "unchecked"}) // class param required for casting. It is NOT unused.
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return (T) getPlugin(pluginName);
    }

    private final File pluginsFolder;
    private final boolean safe;

    public PluginLoader(final File pluginsFolder){
        final LocaleService locale      = Main.getLocale();
        final Map<String,Object> config = Main.getConfig();
        final Logger logger             = Main.getLogger(locale.getString("plugin-loader.name"));

        logger.info(locale.getString("plugin-loader.constructor.start"));

        this.pluginsFolder = Objects.requireNonNull(pluginsFolder);

        if(pluginsFolder.exists() && !pluginsFolder.isDirectory())
            logger.severe(locale.getString("plugin-loader.constructor.dir", pluginsFolder.getPath()));

        if(safe = Boolean.parseBoolean(config.get(ConfigService.SAFE).toString())){
            logger.info(locale.getString("plugin-loader.constructor.safe"));
            return;
        }

        // jar filter
        logger.fine(locale.getString("plugin-loader.filter.jar.start"));
        final Map<File,URL> jars = new JarFilter().filter(pluginsFolder);
        final int init = jars.size();

        // plugin.yml filter + validate
        logger.fine(locale.getString("plugin-loader.filter.yml.start"));
        final Map<File,Map<String,Object>> ymls = new YmlFilter().filter(jars);

        // validate + sort dep
        logger.fine(locale.getString("plugin-loader.filter.dep.start"));
        final Map<File,Map<String,Object>> deps = new DependencyFilter().filter(ymls);

        // enable +verify dependents
        logger.fine(locale.getString("plugin-loader.filter.enable.start"));
        final List<WebDirPlugin> loaded = new PluginInitializer(pluginsFolder).filter(deps);
        logger.fine(locale.getString("plugin-loader.filter.enable.finish", loaded.size(), init));

        // save renderers
        loaded.forEach(plugin -> {
            final String pluginName = plugin.getPluginName();
            plugins.add(plugin);
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
