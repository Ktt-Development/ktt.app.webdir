package com.kttdevelopment.webdir.client.server.unmodifiable;

import com.kttdevelopment.simplehttpserver.*;
import com.sun.net.httpserver.*;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

public final class SimpleHttpServerUnmodifiable extends SimpleHttpServer {

    private final SimpleHttpServer server;

    public SimpleHttpServerUnmodifiable(final SimpleHttpServer server){
        this.server = server;
    }

    @Override
    public final InetSocketAddress getAddress(){
        return server.getAddress();
    }
    
    @Override
    public final HttpContext createContext(final String context){
        return server.createContext(context);
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler){
        return server.createContext(context, handler);
    }

    @Override
    public final HttpContext createContext(final String context, final Authenticator authenticator){
        return server.createContext(context, authenticator);
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler, final Authenticator authenticator){
        return server.createContext(context, handler, authenticator);
    }
    
    @Override
    public final void removeContext(final String context){
        server.removeContext(context);
    }

    @Override
    public final void removeContext(final HttpContext context){
        server.removeContext(context);
    }
    
    @Override
    public final HttpHandler getContextHandler(final String context){
        return server.getContextHandler(context);
    }

    @Override
    public final HttpHandler getContextHandler(final HttpContext context){
        return server.getContextHandler(context);
    }

    @Override
    public final Map<HttpContext,HttpHandler> getContexts(){
        return Collections.unmodifiableMap(server.getContexts());
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
    public final void bind(final InetSocketAddress addr){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void bind(final InetSocketAddress addr, final int backlog){
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final Executor getExecutor(){
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
