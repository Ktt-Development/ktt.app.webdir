package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.render.DefaultFrontMatterLoader;
import com.kttdevelopment.webdir.server.httpserver.SimpleHttpExchangeUnmodifiable;
import com.kttdevelopment.webdir.server.render.ExchangePageRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class StaticFileHandler extends FileHandler {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final ExchangePageRenderer render = new ExchangePageRenderer();

    private final File defaults, source, output;

    public StaticFileHandler(final File defaults, final File source, final File output){
        this.defaults = defaults; this.source = source; this.output = output;
        this.defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,source);
    }

    @Override // target file refers to file in output folder
    public final void handle(final SimpleHttpExchange exchange, final File target, final byte[] bytes) throws IOException{
        final Path rel = target.toPath().relativize(output.toPath());
        final File sourceFile = Paths.get(source.getAbsolutePath(),rel.toString()).toFile();
        exchange.send(render.apply(
            new SimpleHttpExchangeUnmodifiable(exchange),
            sourceFile,
            target,
            defaultFrontMatterLoader.getDefaultFrontMatter(sourceFile),
            bytes
        ));
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("DefaultFileHandler")
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("render",render)
            .addObject("defaults",defaults.getAbsolutePath())
            .addObject("source",source.getAbsolutePath())
            .addObject("output",output.getAbsolutePath())
            .toString();
    }

}
