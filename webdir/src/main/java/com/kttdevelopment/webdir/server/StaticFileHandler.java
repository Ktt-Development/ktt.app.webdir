package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.pluginservice.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

// handles contexts added to the server
public class StaticFileHandler extends FileHandler {

    @SuppressWarnings({"SpellCheckingInspection"})
    @Override // todo: fine logging
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection finalMatter = FrontMatterUtil.loadImports(frontMatter);

            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<FormatterPair> formatters = FrontMatterUtil.getFormatters(config.getList("formatters"));

            final PluginLibrary lib = Application.getPluginService().getLibrary();
            final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

            final Permissions Permissions = Application.getPermissionsService().getPermissions();
            final InetAddress address = exchange.getPublicAddress().getAddress();

            formatters.forEach(formatterPair -> {
                if(formatterPair.getPluginName() == null){
                    try{
                        final PluginFormatterEntry entry = Objects.requireNonNull(lib.getPostFormatter(formatterPair.getFormatterName()));

                        if(Permissions.hasPermission(address, entry.getPermission()))
                            content.set(((PostFormatter) entry.getFormatter()).format(exchange,source, finalMatter, content.get()));
                    }catch(final ClassCastException | NullPointerException ignored){ }
                }else{
                    try{
                        final PluginFormatterEntry entry = Objects.requireNonNull(lib.getPostFormatter(formatterPair.getFormatterName(),formatterPair.getPluginName()));

                        if(Permissions.hasPermission(address, entry.getPermission()))
                            content.set(((PostFormatter) entry.getFormatter()).format(exchange,source,finalMatter,content.get()));
                    }catch(final ClassCastException | NullPointerException ignored){ }
                }
            });

            exchange.send(content.get());
        }else{
            // send literal
            super.handle(exchange, source, bytes);
        }
    }

}
