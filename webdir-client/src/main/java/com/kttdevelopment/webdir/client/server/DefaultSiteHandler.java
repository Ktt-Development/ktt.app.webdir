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

    public DefaultSiteHandler(final PageRenderingService renderer, final SimpleHttpServer server){
        super(new HTMLNameAdapter());
        this.renderer = renderer;
        this.server   = server;
    }

    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        try{
            exchange.send(Objects.requireNonNull(renderer.render(source, server, exchange)).getContentAsBytes());
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
