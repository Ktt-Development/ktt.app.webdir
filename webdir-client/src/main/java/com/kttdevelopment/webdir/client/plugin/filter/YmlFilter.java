package com.kttdevelopment.webdir.client.plugin.filter;

import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.MapUtility;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public final class YmlFilter implements IOFilter<Map<File,URL>,Map<File,Map<String,Object>>> {

    private final LocaleService locale;
    private final Logger logger;

    public YmlFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,Map<String,Object>> filter(final Map<File,URL> in){
        final Map<File,Map<String,Object>> ymls = new LinkedHashMap<>();
        // remove any w/o "plugin.yml" file
        in.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL uyml = Objects.requireNonNull(loader.findResource("plugin.yml"));
                // transform into yaml
                try(final InputStream stream = uyml.openStream()){
                    try{
                        final Map<String,Object> map = MapUtility.asStringObjectMap( new Yaml().load(stream));

                        // validate
                        if(!map.containsKey(PluginLoader.MAIN)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.main", file.getName()));
                            return;
                        }else if(!map.containsKey(PluginLoader.NAME)){
                            logger.severe(locale.getString("plugin-loader.filter.yml.name", file.getName()));
                            return;
                        }else if(map.containsKey(PluginLoader.DEPENDENCIES)){
                            final Object obj =  map.get(PluginLoader.DEPENDENCIES);
                            if(obj instanceof Map<?,?>){
                                logger.severe(locale.getString("plugin-loader.filter.yml.dep", file.getName()));
                                return;
                            }
                        }

                        ymls.put(file, map);
                    }catch(final ClassCastException | YAMLException e){
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
