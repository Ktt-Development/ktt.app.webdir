package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.api.ExchangeRenderer;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import com.kttdevelopment.webdir.generator.server.HTMLNameAdapter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StaticFileHandler extends FileHandler {

    public StaticFileHandler(){
        super(new HTMLNameAdapter());
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{ // handle exchange render only
        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(!frontMatter.hasFrontMatter()){
            exchange.send(bytes);
            return;
        }

        final InetAddress address = exchange.getPublicAddress().getAddress();

        ConfigurationSection tFrontMatter = frontMatter.getFrontMatter();
        tFrontMatter = YamlFrontMatter.loadImports(tFrontMatter);
        tFrontMatter = YamlFrontMatter.loadRelativeImports(source,tFrontMatter);
        final ConfigurationSection finalFrontMatter = tFrontMatter;

        final AtomicReference<String> content = new AtomicReference<>(str);
        final List<Renderer> renderers = YamlFrontMatter.getRenderers(finalFrontMatter.getList("renderers"));
        renderers.forEach(r -> {
            if(!(r instanceof ExchangeRenderer)) return;

            final ExchangeRenderer renderer = (ExchangeRenderer) r;

            //noinspection ConstantConditions
            if(/* todo: hasPermission*/ true)
                content.set(renderer.format(exchange,source,finalFrontMatter,content.get()));
        });

        exchange.send(content.get());
        exchange.close();
    }

}
