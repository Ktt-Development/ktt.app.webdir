package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;

import java.io.File;
import java.io.IOException;

public class DefaultFileHandler extends FileHandler {

    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        super.handle(exchange, source, bytes);
    }

}
