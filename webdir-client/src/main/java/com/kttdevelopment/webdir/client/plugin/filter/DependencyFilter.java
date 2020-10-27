package com.kttdevelopment.webdir.client.plugin.filter;

import com.amihaiemil.eoyaml.*;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class DependencyFilter implements Filter<Map<File,YamlMapping>> {


    private final LocaleService locale;
    private final Logger logger;

    public DependencyFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,YamlMapping> filter(final Map<File,YamlMapping> in){
        final Map<File,YamlMapping> deps = new LinkedHashMap<>();
        {
            final List<String> plugins = new ArrayList<>();
            for(final YamlMapping value : in.values())
                plugins.add(value.string(PluginLoader.NAME));

            // remove missing deps
            in.forEach((file, map) -> {
                for(final YamlNode key : map.keys()){
                    // check if dependency will potentially be loaded
                    if(asString(key).equals(PluginLoader.DEPENDENCIES)){
                        final String name = map.string(PluginLoader.NAME);

                        final String dep = map.string(key);
                        if(dep != null && !plugins.contains(dep))
                            ; // todo: log failure
                        else if(dep == null)
                            for(final YamlNode node : map.yamlSequence(key))
                                if(!plugins.contains(asString(node)))
                                    ; // todo: log failure
                    }
                }
            });

            // remove circular deps

            // sort loading order
        }

        return null;
    }

   private String asString(final YamlNode e){
        return ExceptionUtility.requireNonExceptionElse(() -> e.asScalar().value(), null);
    }

}
