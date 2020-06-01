package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.pluginservice.PluginFormatter;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class StaticFileHandler extends FileHandler {

    @SuppressWarnings({"SpellCheckingInspection", "rawtypes", "unchecked"})
    @Override // todo: fine logging
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("import",String.class);
            final Map OUT = new HashMap();
            imports.forEach(s -> {
                final File IN = new File(Application.parent + '\\' + s);
                try{ OUT.putAll(new ConfigurationFileImpl(IN).toMap());
                }catch(final FileNotFoundException | YamlException ignored){ } // skip if not valid
            });

            OUT.putAll(frontMatter.getFrontMatter().toMap());
            final ConfigurationSection finalMatter = new ConfigurationSectionImpl(OUT);

            final List formattersSrc = config.getList("formatters");

            final List<AbstractFormatterEntry> formattersHead = new LinkedList<>();

            formattersSrc.forEach(o -> { // unify string and map entries
                if(o instanceof String){
                    formattersHead.add(new AbstractFormatterEntry(null, o.toString())); // formatter name only
                }else if(o instanceof Map){
                    final Map obj = (Map) o;
                    try{ // plugin and formatter name
                        formattersHead.add(new AbstractFormatterEntry(Objects.requireNonNull(obj.get("plugin")).toString(), Objects.requireNonNull(obj.get("formatter")).toString()));
                    }catch(final NullPointerException ignored){ }
                }
            });

            final List<PluginFormatter> Formatters = Application.pluginService.getFormatters();
            final Permissions Permissions = Application.permissions.getPermissions();
            final InetAddress address = exchange.getPublicAddress().getAddress();

            final Formatter[] formatters = new Formatter[formattersHead.size()];

            Formatters.forEach(pluginFormatter -> { // iterate through loaded plugins (more efficient)
                final FormatterEntry formatterEntry = pluginFormatter.getEntry();
                for(int i = 0, size = formattersHead.size(); i < size; i++){ // iterate through head to find match
                    if(formatters[i] != null) continue; // skip, already added

                    final AbstractFormatterEntry stringEntry = formattersHead.get(i);
                    if(stringEntry.getFormatterName().equals(formatterEntry.getFormatterName()) && stringEntry.getPluginName() == null || stringEntry.getPluginName().equals(pluginFormatter.getPluginName())){ // matches formatter name with null plugin or matches both
                        final String permission = formatterEntry.getPermission();
                        if(permission == null || Permissions.hasPermission(address, permission)){ // has no permissions or matches permission
                            formatters[i] = formatterEntry.getFormatter(); // formatters are allowed to execute more than once
                        }
                    }
                }
            });

            final List<Formatter> queue = new LinkedList<>(); // remove null
            for(final Formatter formatter : formatters)
                if(formatter != null)
                    queue.add(formatter);

            final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());
            queue.forEach(formatter -> content.set(formatter.format(exchange, source, finalMatter, content.get()))); // execute formatters in order

            exchange.send(content.get());
        }else{
            // send literal
            super.handle(exchange, source, bytes);
        }
    }

}
