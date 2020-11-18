package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DefaultSiteHandler extends FileHandler {

    final PageRenderingService renderer;
    final SimpleHttpServer server;
    final File _404;

    public DefaultSiteHandler(final PageRenderingService renderer, final SimpleHttpServer server, final File _404){
        super(new HTMLNameAdapter());
        this.renderer = renderer;
        this.server   = server;
        this._404     = _404;
    }

    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        try{
            final File index;
            exchange.send(Objects.requireNonNull(
                renderer.render(
                    _404 == null || (source != null &&  source.exists())
                    ? source.isDirectory() && (index = new File(source, "index.html")).exists()
                        ? index
                        : source
                    : _404,
                server,
                exchange)
            ).getContentAsBytes());
        }finally{
            exchange.close();
        }
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("renderers", renderer)
            .toString();
    }

}
