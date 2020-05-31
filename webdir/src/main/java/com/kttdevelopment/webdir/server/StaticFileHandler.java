package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StaticFileHandler extends FileHandler {

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("import",String.class); // import relative file || todo
            final List<String> formatters = config.getList("formatter",String.class);

            final List<Formatter> queue = new ArrayList<>();

            Application.pluginService.getFormatters().values().forEach(
                map -> map.forEach((name, formatter) -> {
                if(formatters.contains(name))
                    queue.add(formatter);
            }));

            final AtomicReference<String> content = new AtomicReference<>(str);

            queue.forEach(formatter -> content.set(formatter.format(exchange, source, frontMatter, content.get())));

            exchange.send(content.get());
        }else{
            // send literal
            super.handle(exchange, source, bytes);
        }
    }

}
