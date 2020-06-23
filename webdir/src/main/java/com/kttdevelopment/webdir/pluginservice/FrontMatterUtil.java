package com.kttdevelopment.webdir.pluginservice;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked", "SpellCheckingInspection"})
public abstract class FrontMatterUtil {

    public static ConfigurationSection loadImports(final YamlFrontMatter frontMatter){
        final ConfigurationSection config = frontMatter.getFrontMatter();

        final List<String> imports = config.getList("import", String.class);
        final Map OUT = new HashMap();
        imports.forEach(s -> {
            final File IN = new File(Application.parent + '\\' + s);

            try{
                final ConfigurationFileImpl impl = new ConfigurationFileImpl(IN);
                impl.load(IN);
                OUT.putAll(impl.toMap());
            }catch(final FileNotFoundException | YamlException ignored){ } // skip if not valid
        });

        OUT.putAll(frontMatter.getFrontMatter().toMap());
        return new ConfigurationSectionImpl(OUT);
    }

    public static List<FormatterPair> getFormatters(final List formatters){
        final List<FormatterPair> pairs = new ArrayList<>();
        formatters.forEach(o -> {
            if(o instanceof String){
                pairs.add(new FormatterPair(null, o.toString())); // formatter name only
            }else if(o instanceof Map){
                final Map obj = (Map) o;
                try{ // plugin and formatter name
                    pairs.add(new FormatterPair(Objects.requireNonNull(obj.get("plugin")).toString(), Objects.requireNonNull(obj.get("formatter")).toString()));
                }catch(final NullPointerException ignored){ }
            }
        });
        return Collections.unmodifiableList(pairs);
    }

}
