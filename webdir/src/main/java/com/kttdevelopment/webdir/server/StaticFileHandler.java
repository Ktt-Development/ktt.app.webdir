package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.PostFormatter;
import com.kttdevelopment.webdir.api.formatter.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.permissions.PermissionsGroup;
import com.kttdevelopment.webdir.pluginservice.*;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

// handles contexts added to the server
public class StaticFileHandler extends FileHandler {

    @SuppressWarnings({"SpellCheckingInspection", "rawtypes", "unchecked"})
    @Override // todo: fine logging
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection finalMatter = FrontMatterUtil.loadImports(frontMatter);

            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<FormatterPair> formatters = FrontMatterUtil.getFormatters(config.getList("formatters"));

            final PluginLibrary lib = Application.pluginService.getLibrary();
            final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

            final Permissions Permissions = Application.permissions.getPermissions();

            formatters.forEach(formatterPair -> {
                if(formatterPair.getPluginName() == null){
                    try{
                        final PluginFormatterEntry entry = Objects.requireNonNull(lib.getPreFormatter(formatterPair.getFormatterName()));

                        if(Permissions.hasPermission((InetAddress) null, entry.getPermission())){
                            // TODO: format!
                        }

                    }catch(final ClassCastException | NullPointerException ignored){ }
                }else{
                    try{
                        final PluginFormatterEntry entry = Objects.requireNonNull(lib.getPreFormatter(formatterPair.getFormatterName(),formatterPair.getPluginName()));
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
