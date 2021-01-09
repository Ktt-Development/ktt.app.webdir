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

package com.kttdevelopment.webdir.client.plugin;

import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarClassLoader {

    private final File file;
    private final JarFile jar;

    public JarClassLoader(final File file) throws IOException{
        this.file = file;
        this.jar  = new JarFile(file);
    }

    private static final int len = ".class".length();

    public final URLClassLoader load() throws MalformedURLException, ClassNotFoundException{
        final URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        final Enumeration<JarEntry> entries = jar.entries();

        while(entries.hasMoreElements()){ // load all .class files from jar
            final JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            if(!entry.isDirectory() && name.endsWith(".class") && !name.equalsIgnoreCase("module-info.class"))
                loader.loadClass(name.substring(0,name.length()-len).replace('/','.'));
        }
        return loader;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("file", file)
            .addObject("jarFile", jar)
            .toString();
    }

}
