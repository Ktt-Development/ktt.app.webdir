package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandlerAdapter;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.PreFormatter;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.pluginservice.*;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultFileAdapter implements FileHandlerAdapter {

    @Override
    public final String getName(final File file){ // remove .html extension from files that are assumed to be web pages
        final String name = file.getName();
        return name.endsWith(".html") ? name.substring(0,name.lastIndexOf(".html")) : name;
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final byte[] getBytes(final File file, final byte[] bytes){
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

                        if(Permissions.hasPermission((InetAddress) null, entry.getPermission()))
                            content.set(((PreFormatter) entry.getFormatter()).format(file, finalMatter, content.get()));
                    }catch(final ClassCastException | NullPointerException ignored){ }
                }else{
                    try{
                        final PluginFormatterEntry entry = Objects.requireNonNull(lib.getPreFormatter(formatterPair.getFormatterName(),formatterPair.getPluginName()));

                        if(Permissions.hasPermission((InetAddress) null, entry.getPermission()))
                            content.set(((PreFormatter) entry.getFormatter()).format(file,finalMatter,content.get()));
                    }catch(final ClassCastException | NullPointerException ignored){ }
                }
            });
            return content.get().getBytes();
        }else{
            return bytes;
        }
    }

}
