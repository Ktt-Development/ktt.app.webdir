package com.kttdevelopment.webdir.httpserver;

import com.kttdevelopment.simplehttpserver.SimpleHttpCookie;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.var.RequestMethod;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

public final class SimpleHttpExchangeUnmodifiable extends SimpleHttpExchange {

    private final SimpleHttpExchange exchange;

    public SimpleHttpExchangeUnmodifiable(final SimpleHttpExchange exchange){
        this.exchange = exchange;
    }

    //

    @Override
    public final HttpServer getHttpServer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final HttpExchange getHttpExchange(){
        throw new UnsupportedOperationException();
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
        return (Headers) Collections.unmodifiableMap(exchange.getRequestHeaders());
    }

    @Override
    public final RequestMethod getRequestMethod(){
        return exchange.getRequestMethod();
    }

    @Override
    public final String getRawGet(){
        return exchange.getRawGet();
    }

    @Override
    public final HashMap<String, String> getGetMap(){
        return (HashMap<String, String>) Collections.unmodifiableMap(exchange.getGetMap());
    }

    @Override
    public final boolean hasGet(){
        return exchange.hasGet();
    }

    @Override
    public final String getRawPost(){
        return exchange.getRawPost();
    }

    @Override
    public final HashMap getPostMap(){
        return (HashMap) Collections.unmodifiableMap(exchange.getPostMap());
    }

    @Override
    public final boolean hasPost(){
        return exchange.hasPost();
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
    public final HashMap<String, String> getCookies(){
        return (HashMap<String, String>) Collections.unmodifiableMap(exchange.getCookies());
    }

    @Override
    public final void setCookie(final String key, final String value){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void setCookie(final SimpleHttpCookie cookie){
        throw new UnsupportedOperationException();
    }

    @Override
    public final OutputStream getOutputStream(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final void sendResponseHeaders(final int code, final long length) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final int responseCode) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] response) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] response, final boolean gzip) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] response, final int responseCode) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final byte[] response, final int responseCode, final boolean gzip) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String response) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String response, final boolean gzip) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String response, final int responseCode) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void send(final String response, final int responseCode, final boolean gzip) throws IOException{
        throw new UnsupportedOperationException();
    }

    @Override
    public final void close(){
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object getAttribute(final String name){
        return exchange.getAttribute(name);
    }

    @Override
    public final void setAttribute(final String name, final Object value){
        throw new UnsupportedOperationException();
    }

}
