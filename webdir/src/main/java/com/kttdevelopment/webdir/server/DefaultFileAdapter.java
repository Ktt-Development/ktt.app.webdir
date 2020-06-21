package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.handler.FileHandlerAdapter;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;
import com.kttdevelopment.webdir.pluginservice.PluginFormatterEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Consumer;

public class DefaultFileAdapter implements FileHandlerAdapter {

    @Override
    public final String getName(final File file){ // remove .html extension from files that are assumed to be web pages
        final String name = file.getName();
        return name.endsWith(".html") ? name.substring(0,name.lastIndexOf(".html")) : name;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final byte[] getBytes(final File file, final byte[] bytes){
        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            // load imports
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("imports", String.class);
            final Map OUT = new HashMap<>();
            imports.forEach(s -> { // import any config files from 'imports' folder
                final File IN = new File(Application.parent + '\\' + s);
                try{ OUT.putAll(new ConfigurationFileImpl(IN).toMap());
                }catch(final FileNotFoundException | YamlException ignored){ } // skip if not valid
            });
            OUT.putAll(frontMatter.getFrontMatter().toMap()); // immediate file overwrites
            // handle formatters
            final ConfigurationSection finalMatter = new ConfigurationSectionImpl(OUT);

            final List<String> formattersSrc = config.getList("formatters",String.class);
            // <Formatter,Plugin>
            final Map<String,String> formattersHead = new HashMap<>();

            config.getList("formatters").forEach(new Consumer() {
                @Override
                public void accept(final Object o){
                    if(o instanceof String){
                        formattersHead.put(o.toString(),null);
                    }
                }
            });

        }else{
            return bytes;
        }
    }

}
