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
