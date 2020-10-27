package com.kttdevelopment.webdir.client.plugin.filter;

import com.amihaiemil.eoyaml.*;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public final class YmlFilter implements IOFilter<Map<File,URL>,Map<File,YamlMapping>> {

    private final LocaleService locale;
    private final Logger logger;

    public YmlFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @Override
    public final Map<File,YamlMapping> filter(final Map<File,URL> in){
        final Map<File,YamlMapping> ymls = new LinkedHashMap<>();
        // remove any w/o "plugin.yml" file
        in.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL uyml = Objects.requireNonNull(loader.findResource("plugin.yml"));
                // transform into yaml
                try(final InputStream stream = uyml.openStream()){
                    try{
                        final YamlMapping map = Yaml.createYamlInput(stream).readYamlMapping();

                        // validate
                        boolean fail = false;
                        try{
                            Objects.requireNonNull(map.string(PluginLoader.MAIN));
                        }catch(final NullPointerException ignored){
                            fail = true;
                            // todo: log err
                        }
                        try{
                            Objects.requireNonNull(map.string(PluginLoader.NAME));
                        }catch(final NullPointerException ignored){
                            fail = true;
                            // todo: log err
                        }

                        for(final YamlNode key : map.keys()){
                            // if contains dependency key then it must be type string or sequence
                            if(asString(key).equals(PluginLoader.DEPENDENCIES)){
                                if(map.string(key) == null || map.yamlSequence(key) == null){
                                    fail = true;
                                    // todo: log err
                                }
                            }
                        }

                        if(!fail){
                            ymls.put(file, map);
                            // todo: log add
                        }
                    }catch(final IOException e){
                        // todo: log failed read
                    }
                }catch(final IOException e){
                    // todo: log failed stream
                }
            }catch(final NullPointerException e){
                // todo: log err
            }catch(final SecurityException | IOException e){
                // todo: log err
            }
        });

        return ymls;
    }

    private String asString(final YamlNode e){
        return ExceptionUtility.requireNonExceptionElse(() -> e.asScalar().value(), null);
    }

}
