package com.kttdevelopment.webdir.client.server.unmodifiable;

import com.kttdevelopment.simplehttpserver.*;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

public final class SimpleHttpExchangeUnmodifiable extends SimpleHttpExchange {

    private final SimpleHttpExchange exchange;

    public SimpleHttpExchangeUnmodifiable(final SimpleHttpExchange exchange){
        this.exchange = exchange;
    }

    @Override
    public final URI getURI(){
        return exchange.getURI();
    }

    @Override
    public final InetSocketAddress getPublicAddress(){
        return exchange.getPublicAddress();
    }

    @Override
    public final InetSocketAddress getLocalAddress(){
        return exchange.getLocalAddress();
    }

    @Override
    public final HttpContext getHttpContext(){
        return new HttpContextUnmodifiable(exchange.getHttpContext());
    }

    @Override
    public final HttpPrincipal getHttpPrincipal(){
        return exchange.getHttpPrincipal();
    }

    @Override
    public final String getProtocol(){
        return exchange.getProtocol();
    }

    @Override
    public final Headers getRequestHeaders(){
        return exchange.getRequestHeaders();
    }

    @Override
    public final String getRequestMethod(){
        return exchange.getRequestMethod();
    }

    @Override
    public final String getRawGet(){
        return exchange.getRawGet();
    }

    @Override
    public final Map<String,String> getGetMap(){
        return Collections.unmodifiableMap(exchange.getGetMap());
    }

    @Override
    public final boolean hasGet(){
        return exchange.hasGet();
    }

    @Override
    public final String getRawPost(){
        return exchange.getRawPost();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final Map getPostMap(){
        return Collections.unmodifiableMap(exchange.getPostMap());
    }

    @Override
    public final MultipartFormData getMultipartFormData(){
        return exchange.getMultipartFormData();
    }

    @Override
    public final boolean hasPost(){
        return exchange.hasPost();
    }


    @Override
    public final Map<String,String> getCookies(){
        return Collections.unmodifiableMap(exchange.getCookies());
    }

    @Override
    public final Object getAttribute(final String s){
        return exchange.getAttribute(s);
    }

    // region unsupported
    
    @Override
    public final HttpServer getHttpServer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpExchange getHttpExchange(){
        throw new UnsupportedOperationException();
    }


    @Override
    public final Headers getResponseHeaders(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final int getResponseCode(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setAttribute(final String s, final Object o){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setCookie(final String s, final String s1){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setCookie(final SimpleHttpCookie simpleHttpCookie){
        throw new UnsupportedOperationException();
    }

    @Override
    public final OutputStream getOutputStream(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void sendResponseHeaders(final int i, final long l){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final int i){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] bytes){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] bytes, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] bytes, final int i){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] bytes, final int i, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String s){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String s, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String s, final int i){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String s, final int i, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final File file){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final File file, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final File file, final int i){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final File file, final int i, final boolean b){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void close(){
        throw new UnsupportedOperationException();
    }
    
    // endregion

}
