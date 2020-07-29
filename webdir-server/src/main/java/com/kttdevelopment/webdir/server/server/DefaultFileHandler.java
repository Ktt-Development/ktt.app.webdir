package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.api.ExchangeRenderer;
import com.kttdevelopment.webdir.generator.render.DefaultFrontMatterLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultFileHandler extends FileHandler {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final ExchangePageRenderer render = new ExchangePageRenderer();
    private final List<ExchangeRenderer> renderers = new ArrayList<>();

    public DefaultFileHandler(final File defaults, final File source){
        this.defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,source);
    }

    @Override // target file refers to file in output folder
    public final void handle(final SimpleHttpExchange exchange, final File target, final byte[] bytes) throws IOException{
        final File source = null; // todo
        exchange.send(render.apply(source,defaultFrontMatterLoader.getDefaultFrontMatter(source),bytes));
    }

}
