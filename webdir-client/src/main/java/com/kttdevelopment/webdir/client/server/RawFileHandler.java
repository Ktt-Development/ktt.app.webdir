package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;

public final class RawFileHandler extends FileHandler {

    private final RootWatchService rootWatchService;

    public RawFileHandler(){
        super(new HTMLNameAdapter());
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
            .addObject("rootWatchService", rootWatchService)
            .toString();
    }

}
