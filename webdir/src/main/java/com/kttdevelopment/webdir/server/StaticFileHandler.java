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
import java.util.function.*;

public class StaticFileHandler extends FileHandler {

    @SuppressWarnings({"SpellCheckingInspection", "rawtypes", "unchecked"})
    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("import",String.class); // import relative file || todo
            final List formattersSrc = config.getList("formatters");

            final List<FormatterEntry> formattersHead = new LinkedList<>();

            for(int index = 0; index < formattersSrc.size(); index++){
                final Object o = formattersSrc.get(index);
                if(o instanceof String){
                    formattersHead.add(new FormatterEntry(index,null,o.toString()));
                }else if(o instanceof Map){
                    final Map obj = (Map) o;
                    if(obj.containsKey("plugin") && obj.containsKey("formatter"))
                        formattersHead.add(new FormatterEntry(index,obj.get("plugin").toString(),obj.get("formatter").toString()));
                }
            }

            final Map<WebDirPlugin, Map<String, Formatter>> Formatters = Application.pluginService.getFormatters();
            final Permissions Permissions = Application.permissions.getPermissions();

            // change how plugins are stored

            Formatters.forEach(new BiConsumer<WebDirPlugin, Map<String, Formatter>>() {
                @Override
                public void accept(final WebDirPlugin plugin, final Map<String, Formatter> stringFormatterMap){
                    final String pluginName = plugin.getPluginService().getPluginName();


                    FormatterEntry match = formattersHead.stream().filter(formatterEntry -> formatterEntry.getPluginName().equals(pluginName) && stringFormatterMap.containsKey(formatterEntry.getFormatterName())).findFirst().orElse(null);
                    if(match != null)
                        match.setAssociatedFormatter(stringFormatterMap.get());

                }
            });

            formattersSrc.forEach(o -> { // no perms access
                if(o instanceof String){
                    final String formatter = (String) o;
                    for(final Map<String,Formatter> entry : Formatters.values()){
                        if(entry.containsKey(formatter)){
                            formattersUnsorted.add(entry.get(formatter));
                            return;
                        }
                    }
                }else if(o instanceof Map){
                    final Map obj = (Map) o;
                    if(obj.containsKey("plugin") && obj.containsKey("formatter")){
                        for(final Map.Entry<WebDirPlugin, Map<String, Formatter>> entry : Formatters.entrySet()){
                            if(entry.getKey().getPluginService().getPluginName().equals(obj.get("plugin"))){
                                final String target = obj.get("formatter").toString();
                                if(entry.getValue().containsKey(target)){
                                    formattersUnsorted.add(entry.getValue().get(target));
                                    return;
                                }
                            }
                        }
                    }
                }
            });


            formattersSrc.forEach(o -> {
                if(o instanceof String){
                    formattersUnsorted.put(null,o.toString());
                }else if(o instanceof Map){
                    final Map obj = (Map) o;
                    if(obj.containsKey("pluginName") && obj.containsKey("formatter"))
                        formattersUnsorted.put(obj.get("pluginName").toString(),obj.get("formatter").toString());
                }
            });

            final Map<WebDirPlugin, Map<String, Formatter>> Formatters = Application.pluginService.getFormatters();
            final Permissions Permissions = Application.permissions.getPermissions();
            final InetAddress addr = exchange.getPublicAddress().getAddress();

            final List<Map.Entry<Integer,Formatter>> queueUnsorted = new ArrayList<>();

            Application.pluginService.getFormatters().forEach(new BiConsumer<WebDirPlugin, Map<String, Formatter>>() {
                @Override
                public void accept(final WebDirPlugin webDirPlugin, final Map<String, Formatter> stringFormatterMap){

                }
            });

            Application.pluginService.getFormatters().forEach(new BiConsumer<WebDirPlugin, Map<String, Formatter>>() {
                @Override
                public void accept(final WebDirPlugin webDirPlugin, final Map<String,Formatter> stringFormatterMap){
                    final Map<Formatter,String> pluginPerm = webDirPlugin.getPermissions();
                    stringFormatterMap.forEach(new BiConsumer<String, Formatter>() {
                        @Override
                        public void accept(final String formatterName, final Formatter formatter){
                            if(
                                !queue.contains(formatter) &&
                                (
                                    headlessFormatters.contains(formatterName) &&
                                    !pluginPerm.containsKey(formatter) ||
                                    Permissions.hasPermission(addr,pluginPerm.get(formatter))
                                )
                            )
                                queue.add(formatter);
                        }
                    });
                }
            });



            Application.pluginService.getFormatters().values().forEach(
                map -> map.forEach((name, formatter) -> {
                if(formattersUnsorted.contains(name))
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
