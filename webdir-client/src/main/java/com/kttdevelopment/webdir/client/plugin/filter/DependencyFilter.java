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

package com.kttdevelopment.webdir.client.plugin.filter;

import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.MapUtility;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class DependencyFilter implements Filter<Map<File,Map<String,Object>>> {


    private final LocaleService locale;
    private final Logger logger;

    public DependencyFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,Map<String,Object>> filter(final Map<File,Map<String,Object>> in){
        final Map<File,Map<String,Object>> deps = new HashMap<>();
        // remove plugins with missing deps
        {
            final List<String> plugins = new ArrayList<>();
            for(final Map<String,Object> value : in.values())
                plugins.add(value.get(PluginLoader.NAME).toString());

            in.forEach((file, map) -> {
                for(final String dependency : getDependencies(map))
                    if(!plugins.contains(dependency))
                        return; // skip add if missing dep
                deps.put(file, map);
            });
        }

        final Map<File,Map<String,Object>> safeDeps = new HashMap<>();
        // remove plugins with circular deps
        {
            final List<Map<String,Object>> plugins = List.copyOf(deps.values());
            deps.forEach((file, yml) -> {
                if(new CircularDependencyChecker(yml, plugins).test()) // if has:
                    logger.severe(locale.getString("plugin-loader.filter.dep.circular", file.getName()));
                else
                    safeDeps.put(file, yml);
            });
        }

        final Map<File,Map<String,Object>> sortDeps = new LinkedHashMap<>();
        // sort dependency loading order
        {
            final int total = safeDeps.size();
            final List<Map.Entry<File,Map<String,Object>>> queue = new ArrayList<>(safeDeps.entrySet());
            int index = 0;

            // sort so dependencies load first, dependents last
            while(sortDeps.size() < total){
                final Map.Entry<File,Map<String,Object>> iterator = queue.get(index);
                final List<Map<String,Object>> unloadedDependencies = getDependencies(iterator.getValue(), new ArrayList<>(safeDeps.values()));

                // remove dependency if it is already loaded
                unloadedDependencies.removeIf(dependency -> {
                    for(final Map<String,Object> dep : sortDeps.values())
                        if(dep.get(PluginLoader.NAME).toString().equals(dependency.get(PluginLoader.NAME).toString()))
                            return true;
                    return false;
                });

                // add to sorted if all dependencies have been loaded (or would be loaded) otherwise move to end of queue
                if(unloadedDependencies.isEmpty())
                    sortDeps.put(iterator.getKey(), iterator.getValue());
                else
                    queue.add(iterator);
                index++;
            }
        }
        return sortDeps;
    }

    static List<String> getDependencies(final Map<String,Object> plugin){
        if(plugin.containsKey(PluginLoader.DEPENDENCIES)){
            final Object obj = plugin.get(PluginLoader.DEPENDENCIES);
            return obj instanceof List<?> ? MapUtility.asStringList((List<?>) obj) : new ArrayList<>(List.of(obj.toString()));
        }
        return new ArrayList<>();
    }

    static List<Map<String,Object>> getDependencies(final Map<String,Object> plugin, final List<Map<String,Object>> plugins){
        final Map<String,Map<String,Object>> map = new HashMap<>();
        for(final Map<String,Object> yml : plugins)
            map.put(yml.get(PluginLoader.NAME).toString(), yml);
        return getDependencies(plugin, map);
    }

    static List<Map<String,Object>> getDependencies(final Map<String,Object> plugin, final Map<String,Map<String,Object>> plugins){
        final List<Map<String,Object>> required = new ArrayList<>();
        for(final String dependency : getDependencies(plugin))
            required.add(plugins.get(dependency));
        return required;
    }

}
