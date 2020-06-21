package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.pluginservice.FormatterPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.BiConsumer;

public class FrontMatterHandler implements BiConsumer<YamlFrontMatter, Formatter> {

    @SuppressWarnings({"unchecked", "SpellCheckingInspection", "rawtypes"})
    @Override
    public final void accept(final YamlFrontMatter frontMatter, final Formatter formatter){
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
            final List<FormatterPair> formattersHead = new ArrayList<>();

            config.getList("formatters").forEach(o -> {
                if(o instanceof String){
                    formattersHead.add(new FormatterPair(null,o.toString()));
                }else if(o instanceof Map){
                    final Map obj = (Map) o;
                    try{
                        formattersHead.add(new FormatterPair(Objects.requireNonNull(obj.get("plugin")).toString(), Objects.requireNonNull(obj.get("formatter")).toString()));
                    }catch(final NullPointerException ignored){ }
                }
            });

        }
    }

}
