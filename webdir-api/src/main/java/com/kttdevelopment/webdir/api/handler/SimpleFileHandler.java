package com.kttdevelopment.webdir.api.handler;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;

public interface SimpleFileHandler {

    byte[] handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes);

}
