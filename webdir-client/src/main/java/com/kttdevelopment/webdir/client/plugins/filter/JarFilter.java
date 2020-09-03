package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.function.IOFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class JarFilter implements IOFilter<File,Map<File,URL>> {

    private final LocaleService locale;
    private final Logger logger;

    public JarFilter(){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));
    }

    @Override
    public final Map<File,URL> filter(final File dir){
        // remove any w/o '.jar' ext
        final File[] jars = Objects.requireNonNullElse(dir.listFiles(pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar")),new File[0]);
        Arrays.sort(jars);

        // remove any malformed URL
        final Map<File,URL> map = new LinkedHashMap<>();
        for(final File file : jars){
            try{
                map.put(file,file.toURI().toURL());
                logger.finest(locale.getString("pluginLoader.jarFilter.validFile", file));
            }catch(final IllegalArgumentException | MalformedURLException e){
                logger.severe(locale.getString("pluginLoader.jarFilter.malformedURL",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.jarFilter.accessDenied",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }
        return map;
    }

}
