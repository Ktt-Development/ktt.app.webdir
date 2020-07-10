package com.kttdevelopment.webdir.server.httpserver;

import com.kttdevelopment.simplehttpserver.*;
import com.sun.net.httpserver.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;

public final class SimpleHttpServerUnmodifiable extends SimpleHttpServer {

    private final SimpleHttpServer server;

    public SimpleHttpServerUnmodifiable(final SimpleHttpServer server){
        this.server = server;
    }
    
    //

    private final Map<HttpContext,HttpHandler> contexts = new HashMap<>();

    //

    @Override
    public final InetSocketAddress getAddress(){
        return server.getAddress();
    }

    @Override
    public final Executor getExecutor(){
        return server.getExecutor();
    }

    // only allow operations to plugin added contexts
    @Override
    public final HttpContext createContext(final String context){
        return createContext(context, (HttpExchange exchange) -> {},null);
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler){
        return createContext(context,handler,null);
    }

    @Override
    public final HttpContext createContext(final String context, final Authenticator authenticator){
        return createContext(context,(HttpExchange exchange) -> {},authenticator);
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler, final Authenticator authenticator){
        final HttpContext hc = new HttpContextUnmodifiable(server.createContext(context,handler));
        contexts.put(hc,hc.getHandler());
        if(authenticator != null)
            hc.setAuthenticator(authenticator);
        return hc;
    }

    @Override
    public final void removeContext(final String context){
        for(final HttpContext hc : new HashSet<>(contexts.keySet())){
            if(hc.getPath().equalsIgnoreCase(context)){
                removeContext(hc);
                return;
            }
        }
    }

    @Override
    public final void removeContext(final HttpContext context){
        for(final HttpContext hc : new HashSet<>(contexts.keySet())){
            if(context == hc){
                contexts.remove(hc);
                server.removeContext(hc);
                return;
            }
        }
    }

    @Override
    public final HttpHandler getContextHandler(final String context){
        for(final Map.Entry<HttpContext, HttpHandler> entry : new HashSet<>(contexts.entrySet()))
            if(entry.getKey().getPath().equalsIgnoreCase(context))
                return entry.getValue();
        return null;
    }

    @Override
    public final HttpHandler getContextHandler(final HttpContext context){
        for(final Map.Entry<HttpContext, HttpHandler> entry : new HashSet<>(contexts.entrySet()))
            if(context == entry.getKey())
                return entry.getValue();
        return null;
    }

    @Override
    public final Map<HttpContext,HttpHandler> getContexts(){
        return Collections.unmodifiableMap(contexts);
    }

    @Override
    public final String getRandomContext(){
        return server.getRandomContext();
    }

    @Override
    public final String getRandomContext(final String context){
        return server.getRandomContext(context);
    }

    // region unsupported

    @Override
    public final HttpServer getHttpServer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final InetSocketAddress bind(final int port){
        throw new UnsupportedOperationException();
    }

    @Override
    public final InetSocketAddress bind(final int port, final int backlog){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void bind(final InetSocketAddress address){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void bind(final InetSocketAddress address, final int backlog){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setExecutor(final Executor executor){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setHttpSessionHandler(final HttpSessionHandler sessionHandler){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpSessionHandler getHttpSessionHandler(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpSession getHttpSession(final HttpExchange exchange){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpSession getHttpSession(final SimpleHttpExchange exchange){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void start(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void stop(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void stop(final int delay){
        throw new UnsupportedOperationException();
    }

    // endregion

}
