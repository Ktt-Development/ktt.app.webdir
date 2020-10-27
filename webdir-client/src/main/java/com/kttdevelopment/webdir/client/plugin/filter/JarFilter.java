package com.kttdevelopment.webdir.client.plugin.filter;

import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.Main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class JarFilter implements IOFilter<File,Map<File,URL>> {

    private final LocaleService locale;
    private final Logger logger;

    public JarFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @Override
    public final Map<File, URL> filter(final File in){
        // files ending with ".jar"
        final File[] jars = Objects.requireNonNullElse(in.listFiles(p -> p.isFile() && p.getName().toLowerCase().endsWith(".jar")), new File[0]);
        Arrays.sort(jars);

        // remove any malformed URL
        final Map<File,URL> map = new LinkedHashMap<>();
        for(final File jar : jars)
            try{
                map.put(jar, jar.toURI().toURL());
            }catch(final IllegalArgumentException | MalformedURLException | SecurityException e){
                // todo: log err
            }

        return map;
    }

}
