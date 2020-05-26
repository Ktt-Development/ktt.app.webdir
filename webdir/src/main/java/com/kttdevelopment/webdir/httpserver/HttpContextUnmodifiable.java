package com.kttdevelopment.webdir.httpserver;

import com.sun.net.httpserver.*;

import java.util.*;

public final class HttpContextUnmodifiable extends HttpContext {

    private final HttpContext context;

    public HttpContextUnmodifiable(final HttpContext context){
        this.context = context;
    }

    //

    @Override
    public final HttpHandler getHandler(){
        return context.getHandler();
    }

    @Override
    public final void setHandler(final HttpHandler h){
        context.setHandler(h);
    }

    @Override
    public final String getPath(){
        return context.getPath();
    }

    @Override
    public final Map<String, Object> getAttributes(){
        return Collections.unmodifiableMap(context.getAttributes());
    }

    @Override
    public final List<Filter> getFilters(){
        return Collections.unmodifiableList(context.getFilters());
    }

    @Override
    public final Authenticator setAuthenticator(final Authenticator auth){
        return context.setAuthenticator(auth);
    }

    @Override
    public final Authenticator getAuthenticator(){
        return context.getAuthenticator();
    }

    // unsupported
    @Override
    public final HttpServer getServer(){
        throw new UnsupportedOperationException();
    }

}
