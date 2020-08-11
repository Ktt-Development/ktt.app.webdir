package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.render.DefaultFrontMatterLoader;
import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.httpserver.SimpleHttpExchangeUnmodifiable;
import com.kttdevelopment.webdir.server.render.FilePageRenderer;

import java.io.File;
import java.io.IOException;

public final class DefaultFileHandler extends FileHandler {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final FilePageRenderer render = new FilePageRenderer();

    private final File defaults, source;

    public DefaultFileHandler(final File defaults){
        this.defaults = defaults;
        this.source   = new File(Vars.Main.getConfigService().getConfig().getString(ServerVars.Config.filesContextKey, ServerVars.Config.defaultFilesContext));
        this.defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults, source);
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        exchange.send(render.apply(
            new SimpleHttpExchangeUnmodifiable(exchange),
            source,
            defaultFrontMatterLoader.getDefaultFrontMatter(source.getAbsolutePath()),
            bytes
        ));
        exchange.close();
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("DefaultFileHandler")
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("render",render)
            .addObject("defaults",defaults.getAbsolutePath())
            .addObject("source",source.getAbsolutePath())
            .toString();
    }

}
