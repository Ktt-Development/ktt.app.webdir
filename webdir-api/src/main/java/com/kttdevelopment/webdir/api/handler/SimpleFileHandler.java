package com.kttdevelopment.webdir.api.handler;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;
import java.util.function.BiPredicate;

public interface SimpleFileHandler extends BiPredicate<SimpleHttpExchange,File> {

    byte[] handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes);

}
