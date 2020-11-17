package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;

public final class DefaultFileHandler extends DefaultSiteHandler {

    private final RootWatchService rootWatchService;

    public DefaultFileHandler(final PageRenderingService renderer, final SimpleHttpServer server){
        super(renderer, server, null);
        this.rootWatchService = new RootWatchService(this);
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        rootWatchService.check();
        super.handle(exchange, source, bytes);
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("renderers", renderer)
            .addObject("rootWatchService", rootWatchService)
            .toString();
    }

}
