package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;

public interface SimpleFileHandler {

    boolean canFormat(SimpleHttpExchange exchange, File file);

    byte[] handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes);

}
