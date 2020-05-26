package com.kttdevelopment.webdir.simplehttpserver;

import com.kttdevelopment.simplehttpserver.*;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;

public final class SimpleHttpServerUnmodifiable extends SimpleHttpServer {

    private final SimpleHttpServer server;

    public SimpleHttpServerUnmodifiable(final SimpleHttpServer server){
        this.server = server;
    }
    
    //

    @Override
    public final HttpServer getHttpServer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final InetSocketAddress bind(final int port) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final InetSocketAddress bind(final int port, final int backlog) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void bind(final InetSocketAddress addr) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void bind(final InetSocketAddress addr, final int backlog) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final InetSocketAddress getAddress(){
        return server.getAddress();
    }

    @Override
    public final void setExecutor(final Executor executor){
        throw new UnsupportedOperationException();
    }

    @Override
    public final Executor getExecutor(){
        return server.getExecutor();
    }

    @Override
    public final void setHttpSessionHandler(final HttpSessionHandler sessionHandler){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpSessionHandler getHttpSessionHandler(){
        return server.getHttpSessionHandler();
    }

    @Override
    public final HttpSession getHttpSession(final HttpExchange exchange){
        return server.getHttpSession(exchange);
    }

    @Override
    public final HttpSession getHttpSession(final SimpleHttpExchange exchange){
        return server.getHttpSession(exchange);
    }
    // only allow operations to plugin added contexts
    @Override
    public final HttpContext createContext(final String context){
        return null;
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler){
        return null;
    }

    @Override
    public final HttpContext createContext(final String context, final Authenticator authenticator){
        return null;
    }

    @Override
    public final HttpContext createContext(final String context, final HttpHandler handler, final Authenticator authenticator){
        return null;
    }

    @Override
    public final void removeContext(final String context){

    }

    @Override
    public final void removeContext(final HttpContext context){

    }
    // potential security risk here
    @Override
    public final HttpHandler getContextHandler(final String context){
        return server.getContextHandler(context);
    }

    @Override
    public final HttpHandler getContextHandler(final HttpContext context){
        return server.getContextHandler(context);
    }

    @Override
    public final Map<HttpContext, HttpHandler> getContexts(){
        return null;
    }

    @Override
    public final String getRandomContext(){
        return server.getRandomContext();
    }

    @Override
    public final String getRandomContext(final String context){
        return server.getRandomContext(context);
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

}
