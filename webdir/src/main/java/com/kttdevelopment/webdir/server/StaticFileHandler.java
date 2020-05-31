package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StaticFileHandler extends FileHandler {

    @SuppressWarnings({"SpellCheckingInspection", "rawtypes", "unchecked"})
    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("import",String.class); // import relative file || todo
            final List formatters = config.getList("formatter");

            final List<String> headlessFormatters = new ArrayList<>();
            final Map<String,String> headedFormatters = new LinkedHashMap<>();

            formatters.forEach(o -> {
                if(o instanceof String)
                    headlessFormatters.add(o.toString());
                else if(o instanceof Map){
                    final Map obj = (Map) o;
                    if(obj.containsKey("pluginName") && obj.containsKey("formatter"))
                        headedFormatters.put(obj.get("pluginName").toString(),obj.get("formatter").toString());
                }
            });

            final Permissions perms = Application.permissions.getPermissions();
            final InetAddress addr = exchange.getPublicAddress().getAddress();
            final List<Formatter> queue = new LinkedList<>();

            Application.pluginService.getFormatters().forEach(new BiConsumer<WebDirPlugin, Map<String, Formatter>>() {
                @Override
                public void accept(final WebDirPlugin webDirPlugin, final Map<String,Formatter> stringFormatterMap){
                    final Map<Formatter,String> pluginPerm = webDirPlugin.getPermissions();
                    stringFormatterMap.forEach(new BiConsumer<String, Formatter>() {
                        @Override
                        public void accept(final String s, final Formatter formatter){
                            if(headlessFormatters.contains(s) && perms.hasPermission(addr,pluginperm.get(formatter)));
                        }
                    });
                }
            });



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
