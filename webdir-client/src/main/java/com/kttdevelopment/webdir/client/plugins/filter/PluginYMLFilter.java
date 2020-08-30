package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.function.Filter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public final class PluginYMLFilter implements Filter<Map<File,URL>> {

    private final LocaleService locale;
    private final Logger logger;

    public PluginYMLFilter(){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));
    }

    @Override
    public final Map<File, URL> filter(final Map<File,URL> in){
        final Map<File,URL> map = new LinkedHashMap<>();
        // remove any w/o 'plugin.yml'
        in.forEach((file,url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL yml = Objects.requireNonNull(loader.findResource("plugin.yml"));
                map.put(file,yml);
                logger.finest(locale.getString("pluginLoader.pluginYMLFilter.validFile",file));
            }catch(final NullPointerException e){
                logger.severe(locale.getString("pluginLoader.pluginYMLFilter.notFound",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.pluginYMLFilter.accessDenied",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.pluginYMLFilter.failedClose",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }
        });
        return map;
    }

}
