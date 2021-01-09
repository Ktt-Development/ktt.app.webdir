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

package com.kttdevelopment.webdir.client.server.unmodifiable;

import com.sun.net.httpserver.*;

import java.util.*;

public final class HttpContextUnmodifiable extends HttpContext {

    private final HttpContext context;

    public HttpContextUnmodifiable(final HttpContext context){
        this.context = context;
    }

    @Override
    public final HttpHandler getHandler(){
        return context.getHandler();
    }

    @Override
    public final String getPath(){
        return context.getPath();
    }

    @Override
    public final Map<String,Object> getAttributes(){
        return Collections.unmodifiableMap(context.getAttributes());
    }

    @Override
    public final List<Filter> getFilters(){
        return Collections.unmodifiableList(context.getFilters());
    }

    @Override
    public final Authenticator getAuthenticator(){
        return context.getAuthenticator();
    }

    // region unsupported

    @Override
    public final HttpServer getServer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setHandler(final HttpHandler h){
        throw new UnsupportedOperationException();
    }

    @Override
    public final Authenticator setAuthenticator(final Authenticator auth){
        throw new UnsupportedOperationException();
    }

    // endregion

}
