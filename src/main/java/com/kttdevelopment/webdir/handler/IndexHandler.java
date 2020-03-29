package com.kttdevelopment.webdir.handler;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpHandler;
import com.kttdevelopment.simplehttpserver.handler.RootHandler;
import com.kttdevelopment.webdir.main.Directory;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class IndexHandler extends RootHandler {

    public IndexHandler(final HttpHandler rootHandler, final HttpHandler elseHandler){
        super(new RootHandler(), new NotFoundHandler());
    }

    static class RootHandler extends SimpleHttpHandler {

        @Override
        public void handle(final SimpleHttpExchange exchange) throws IOException{
            // find index file
        }

    }

    static class NotFoundHandler extends SimpleHttpHandler {

        @Override
        public void handle(final SimpleHttpExchange exchange) throws IOException{

        }

    }

}
