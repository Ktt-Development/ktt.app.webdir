package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.render.DefaultFrontMatterLoader;

import java.io.File;
import java.io.IOException;

public class DefaultFileHandler extends FileHandler {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    // private final ExchangePageRenderer render = new ExchangePageRenderer();

    private final File defaults, source, output;

    public DefaultFileHandler(final File defaults, final File source, final File output){
        this.defaults = defaults; this.source = source; this.output = output;
        this.defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,source);
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String rel = ContextUtil.getContext(exchange.getHttpContext().getPath(),true,false);
    }

}
