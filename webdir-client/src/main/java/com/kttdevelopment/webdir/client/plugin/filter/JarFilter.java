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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class JarFilter implements IOFilter<File,Map<File,URL>> {

    private final LocaleService locale;
    private final Logger logger;

    public JarFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @Override
    public final Map<File, URL> filter(final File in){
        // files ending with ".jar"
        final File[] jars = Objects.requireNonNullElse(in.listFiles(p -> p.isFile() && p.getName().toLowerCase().endsWith(".jar")), new File[0]);
        Arrays.sort(jars);

        // remove any malformed URL
        final Map<File,URL> map = new LinkedHashMap<>();
        for(final File jar : jars)
            try{
                map.put(jar, jar.toURI().toURL());
            }catch(final IllegalArgumentException | MalformedURLException | SecurityException e){
                logger.severe(locale.getString("plugin-loader.filter.jar.malformed", jar.getName()) + LoggerService.getStackTraceAsString(e));
            }

        return map;
    }

}
