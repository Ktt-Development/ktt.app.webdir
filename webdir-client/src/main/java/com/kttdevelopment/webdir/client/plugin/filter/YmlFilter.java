package com.kttdevelopment.webdir.client.plugin.filter;

import com.amihaiemil.eoyaml.*;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.YamlUtility;

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
                        if(!YamlUtility.containsKey(PluginLoader.MAIN, map)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.main", file.getName()));
                            return;
                        }else if(!YamlUtility.containsKey(PluginLoader.NAME, map)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.name", file.getName()));
                            return;
                        }else if(YamlUtility.containsKey(PluginLoader.DEPENDENCIES, map)){
                            final Node type = map.value(PluginLoader.DEPENDENCIES).type();
                            if(type != Node.SCALAR && type != Node.MAPPING){
                                logger.severe(locale.getString("plugin-loader.filter.yml.dep", file.getName()));
                                return;
                            }
                        }

                        ymls.put(file, map);
                    }catch(final IOException e){
                        logger.severe(locale.getString("plugin-loader.filter.yml.yml", file.getName()) + LoggerService.getStackTraceAsString(e));
                    }
                }catch(final IOException e){
                    logger.severe(locale.getString("plugin-loader.filter.yml.url", file.getName()) + LoggerService.getStackTraceAsString(e));
                }
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("plugin-loader.filter.yml.null", file.getName()));
            }catch(final SecurityException | IOException e){
                logger.severe(locale.getString("plugin-loader.filter.yml.yml", file.getName()) + LoggerService.getStackTraceAsString(e));
            }
        });

        return ymls;
    }

}
