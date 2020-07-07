package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.object.TriTuple;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginServiceImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PluginLoader {

    private static final String mainClassName = "main";

    protected Consumer<WebDirPlugin> loader = plugin -> {
        plugin.onEnable();
    };

    @SuppressWarnings("unchecked")
    PluginLoader(){
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Logger.getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const"));

        // config pluginFolder
        final File pluginFolder = new File("/plugins");

        // config safemode

        // load jar files
        final Map<File,URL> pluginJars = new HashMap<>();
        {
            final File[] plugins =  Objects.requireNonNullElse(
                pluginFolder.listFiles((dir, name) -> !dir.isDirectory() && name.toLowerCase().endsWith(".jar")),
                new File[0]
            );
            for(final File file : plugins){
                try{
                    pluginJars.put(file,file.toURI().toURL());
                }catch(final MalformedURLException | IllegalArgumentException e){
                    logger.severe(locale.getString("pluginLoader.const.badURL", file.getName() + '\n' + Exceptions.getStackTraceAsString(e)));
                }
            }
        }
        final int initialPluginCount = pluginJars.size();

        // load plugin.yml
        final Map<File,URL> pluginYMLs = new HashMap<>();
        pluginJars.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL yml = Objects.requireNonNull(loader.findResource("plugin.yml"));
                pluginYMLs.put(file,yml);
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.const.UCLSec",file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.nullYML",file.getName()));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.UCLCloseIO", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        // load plugin main
        final List<TriTuple<File,Class<WebDirPlugin>,ConfigurationSection>> plugins = new ArrayList<>();

        pluginYMLs.entrySet().removeIf(entry -> {
            final File plugin = entry.getKey();
            final String pluginName = plugin.getName();
            final URL ymlURL = entry.getValue();

            final ConfigurationSection yml;

            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(ymlURL.openStream()));
                //noinspection rawtypes
                yml = new ConfigurationSectionImpl((Map) IN.read());
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("pluginLoader.const.badYMLSyntax",pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return true;
            }catch(final IOException e){
                logger.severe(locale.getString("pluginLoader.const.ymlStreamIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return true;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginLoader.const.streamClose",pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }

            try(final URLClassLoader loader = new URLClassLoader(new URL[]{plugin.toURI().toURL()})){
                plugins.add(new TriTuple<>(plugin,(Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(mainClassName)),yml));
            }catch(MalformedURLException | IllegalArgumentException e){
                logger.severe(locale.getString("pluginLoader.const.UCLSec",pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return true;
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.noMainClass",pluginName));
                return true;
            }catch(final ClassCastException ignored){
                logger.severe(locale.getString("pluginLoader.const.badMainCast",pluginName));
                return true;
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.UCLCloseIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }

            return false;
        });

        // initialize plugins
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final AtomicInteger loadedPlugins = new AtomicInteger(0);
        final int timeout = 30;
        final TimeUnit unit = TimeUnit.SECONDS;

        plugins.forEach(tuple -> {
            final File pluginFile = tuple.getVar1();
            final Class<WebDirPlugin> mainClass = tuple.getVar2();
            final ConfigurationSection yml = tuple.getVar3();

            final Future<?> future = executor.submit(() -> {
                try{
                    final PluginService provider = new PluginServiceImpl(yml);
                    final String pluginName = provider.getPluginYml().getPluginName();
                    final Logger logger1 = Logger.getLogger(pluginName);

                    try{
                        final WebDirPlugin plugin = mainClass.getDeclaredConstructor(PluginService.class).newInstance(provider);
                        loader.accept(plugin);
                        loadedPlugins.incrementAndGet();
                    }catch(final InstantiationException ignored){
                        logger1.severe(locale.getString("pluginLoader.loader.abstract", pluginName));
                    }catch(final IllegalAccessException ignored){
                        logger1.severe(locale.getString("pluginLoader.loader.scope", pluginName));
                    }catch(final NoSuchMethodException | IllegalArgumentException ignored){
                        logger1.severe(locale.getString("pluginLoader.loader.constArgs", pluginName));
                    }catch(final ExceptionInInitializerError | InvocationTargetException e){
                        logger1.severe(locale.getString("pluginLoader.loader.const", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger1.severe(locale.getString("pluginLoader.loader.sec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                }catch(final NullPointerException ignored){
                    Logger.getLogger(pluginFile.getName()).severe(locale.getString("pluginLoader.loader.noName",pluginFile.getName()));
                }
            });

            try{
                future.get(timeout,unit);
            }catch(final Exception e){
                future.cancel(true);
                if(e instanceof TimeoutException)
                    logger.severe(locale.getString("pluginLoader.loader.timedOut",pluginFile.getName(),timeout + ' ' + unit.name().toLowerCase()));
                else
                    logger.severe(locale.getString("pluginLoader.loader.unknown",pluginFile.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });


        executor.shutdown();
        logger.info(locale.getString("pluginLoader.const.loaded",loadedPlugins.get(),initialPluginCount));
    }
}
