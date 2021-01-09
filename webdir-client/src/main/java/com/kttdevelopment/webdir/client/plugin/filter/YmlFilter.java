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

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.MapUtility;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public final class YmlFilter implements IOFilter<Map<File,URL>,Map<File,Map<String,Object>>> {

    private final LocaleService locale;
    private final Logger logger;

    public YmlFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,Map<String,Object>> filter(final Map<File,URL> in){
        final Map<File,Map<String,Object>> ymls = new LinkedHashMap<>();
        // remove any w/o "plugin.yml" file
        in.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL uyml = Objects.requireNonNull(loader.findResource("plugin.yml"));
                // transform into yaml
                try(final InputStreamReader IN = new InputStreamReader(uyml.openStream())){
                    try{
                        final Map<String,Object> map = MapUtility.asStringObjectMap( (Map<?,?>) new YamlReader(IN).read());

                        // validate
                        if(!map.containsKey(PluginLoader.MAIN)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.main", file.getName()));
                            return;
                        }else if(!map.containsKey(PluginLoader.NAME)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.name", file.getName()));
                            return;
                        }else if(map.containsKey(PluginLoader.DEPENDENCIES)){
                            final Object obj =  map.get(PluginLoader.DEPENDENCIES);
                            if(obj instanceof Map<?,?>){
                                logger.severe(locale.getString("plugin-loader.filter.yml.dep", file.getName()));
                                return;
                            }
                        }

                        ymls.put(file, map);
                    }catch(final ClassCastException | YamlException e){
                        logger.severe(locale.getString("plugin-loader.filter.yml.yml", file.getName()) + LoggerService.getStackTraceAsString(e));
                    }
                }catch(final IOException e){
                    logger.severe(locale.getString("plugin-loader.filter.yml.url", file.getName()) + LoggerService.getStackTraceAsString(e));
                }
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("plugin-loader.filter.yml.null", file.getName()));
            }catch(final SecurityException | IOException e){
                logger.severe(locale.getString("plugin-loader.filter.yml.yml", file.getName()) + LoggerService.getStackTraceAsString(e));
            }
        });

        return ymls;
    }

}
