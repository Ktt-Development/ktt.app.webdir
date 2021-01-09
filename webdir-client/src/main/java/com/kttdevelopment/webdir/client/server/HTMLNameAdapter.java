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

package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandlerAdapter;

import java.io.File;

public final class HTMLNameAdapter implements FileHandlerAdapter {

    private final int len = ".html".length();

    @Override
    public final String getName(final File file){
        final String name  = file.getName();
        final String rhtml = name.toLowerCase().endsWith(".html") ? name.substring(0, name.length()-len) : name;
        return file.isDirectory() || rhtml.isBlank() || rhtml.equalsIgnoreCase(".html") ? name : rhtml;
    }

}
