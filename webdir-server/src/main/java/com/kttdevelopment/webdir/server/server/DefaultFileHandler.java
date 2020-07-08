package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;

import java.io.File;
import java.io.IOException;

public class DefaultFileHandler extends FileHandler {

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{

        // todo:
        // find handler that passes test
        // check if permission
        // apply with unmodifiable version
        // send (do not allow more than one handler to process)

        super.handle(exchange, source, bytes);
    }

}
