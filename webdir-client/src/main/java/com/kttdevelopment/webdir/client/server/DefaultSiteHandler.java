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

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DefaultSiteHandler extends FileHandler {

    final PageRenderingService renderer;
    final SimpleHttpServer server;
    final File _404;

    public DefaultSiteHandler(final PageRenderingService renderer, final SimpleHttpServer server, final File _404){
        super(new HTMLNameAdapter());
        this.renderer = renderer;
        this.server   = server;
        this._404     = _404;
    }

    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        try{
            final File index;
            exchange.send(Objects.requireNonNull(
                renderer.render(
                    _404 == null || !_404.exists() || (source != null &&  source.exists())
                    ? source.isDirectory() && (index = new File(source, "index.html")).exists()
                        ? index
                        : source
                    : _404,
                server,
                exchange)
            ).getContentAsBytes());
        }finally{
            exchange.close();
        }
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("renderers", renderer)
            .toString();
    }

}
