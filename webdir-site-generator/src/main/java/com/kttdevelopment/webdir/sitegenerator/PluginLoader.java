package com.kttdevelopment.webdir.sitegenerator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.sitegenerator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.sitegenerator.function.Exceptions;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public final class PluginLoader {

    private static final String mainClassName = "main";

    @SuppressWarnings("unchecked")
    PluginLoader(){
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Logger.getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginService.const"));

        // config pluginFolder
        final File pluginFolder = new File("/plugins");

        // config safemode

        // load jar files
        final List<URL> pluginURLs = new ArrayList<>();
        final int totalPlugins = pluginURLs.size();
        {
            final File[] plugins =  Objects.requireNonNullElse(
                pluginFolder.listFiles((dir, name) -> !dir.isDirectory() && name.toLowerCase().endsWith(".jar")),
                new File[0]
            );
            for(final File file : plugins){
                try{
                    pluginURLs.add(file.toURI().toURL());
                }catch(final MalformedURLException e){
                    logger.severe(locale.getString("pluginService.const.badURL", file.getName() + '\n' + Exceptions.getStackTraceAsString(e)));
                }
            }
        }

        // load plugin.yml
        final Enumeration<URL> resourceURLs;
        final URLClassLoader loader = new URLClassLoader(pluginURLs.toArray(new URL[0]));
        {
            try{
                resourceURLs = loader.findResources("plugin.yml");
            }catch(final IOException e){
                logger.severe(locale.getString("pluginService.const.pluginURLs") + '\n' + Exceptions.getStackTraceAsString(e));
                try{ loader.close(); // close if fails here
                }catch(IOException ignored){ }
                return;
            }
        }

        // load plugin main
        final Map<Class<WebDirPlugin>,ConfigurationSection> plugins = new LinkedHashMap<>();
        while(resourceURLs.hasMoreElements()){
            // locate main
            final URL ymlURL = resourceURLs.nextElement();
            final String ymlURLstr = ymlURL.toString();
            final ConfigurationSection yml;

            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(ymlURL.openStream()));
                yml = new ConfigurationSectionImpl((Map) IN.read());
            }catch(final ClassCastException | YamlException e){
                logger.warning(locale.getString("pluginService.const.badYml",ymlURLstr)+ '\n' + Exceptions.getStackTraceAsString(e));
                continue;
            }catch(final IOException e){
                logger.warning(locale.getString("pluginService.const.badYmlIO",ymlURLstr) + '\n' + Exceptions.getStackTraceAsString(e));
                continue;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginService.const.badCloseIO",ymlURLstr) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }

            // load main
            try{
                plugins.put((Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(mainClassName)),yml);
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.warning(locale.getString("pluginService.const.missingMain",ymlURLstr));
            }catch(final ClassCastException e){
                logger.warning(locale.getString("pluginService.const.badMainCast",ymlURLstr) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        }

        // initialize plugins
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final AtomicInteger loadedPlugins = new AtomicInteger(0);
        plugins.forEach((mainClass, pluginYml) -> {
            final Future<?> future = executor.submit(() -> {

                loadedPlugins.incrementAndGet();
            });

            try{
                future.get(30,TimeUnit.SECONDS);
            }catch(final Exception e){
                future.cancel(true);

            }
        });

        executor.shutdown();
        logger.info(locale.getString("pluginService.const.loaded",loadedPlugins.get(),totalPlugins));
    }
}
