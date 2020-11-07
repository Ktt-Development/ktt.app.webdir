package com.kttdevelopment.webdir.client.plugin.filter;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.plugin.JarClassLoader;
import com.kttdevelopment.webdir.client.plugin.PluginServiceImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class PluginInitializer implements IOFilter<Map<File,YamlMapping>,List<WebDirPlugin>> {

    private final LocaleService locale;
    private final Logger logger;
    private final File pluginFolder;

    public PluginInitializer(final File pluginFolder){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
        this.pluginFolder = pluginFolder;
    }

    @Override
    public final List<WebDirPlugin> filter(final Map<File,YamlMapping> in){
        final List<WebDirPlugin> loaded = new ArrayList<>();
        in.forEach((file, yml) -> {
            // check that all dependencies have been loaded
            {
                final List<String> missingDependencies = DependencyFilter.getDependencies(yml);
                missingDependencies.removeIf(name -> {
                    for(final WebDirPlugin entry : loaded)
                        if(entry.getPluginName().equals(name))
                            return true;
                    return false;
                });

                if(!missingDependencies.isEmpty()){
                    logger.severe(locale.getString("plugin-loader.filter.enable.dep", file.getName(), missingDependencies.toString()));
                    return;
                }
            }

            // load jar
            {
                try(final URLClassLoader loader = new JarClassLoader(file).load()){
                    final ExecutorService executor = Executors.newSingleThreadExecutor();
                    final Future<WebDirPlugin> future = executor.submit(() -> {
                        WebDirPlugin plugin = null;
                        try{
                            plugin = (WebDirPlugin) loader
                                .loadClass(yml.string(PluginLoader.MAIN))
                                .getDeclaredConstructor(PluginService.class)
                                .newInstance(new PluginServiceImpl(yml, pluginFolder));
                        }catch(final ClassCastException e){
                            logger.severe(locale.getString("plugin-loader.filter.enable.class", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final ClassNotFoundException e){ // .loadClass
                            logger.severe(locale.getString("plugin-loader.filter.enable.missing", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final NoSuchMethodException e){ // .getDeclaredConstructor
                            logger.severe(locale.getString("plugin-loader.filter.enable.constructor", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final SecurityException e){
                            logger.severe(locale.getString("plugin-loader.filter.enable.access", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final IllegalAccessException e){ // .newInstance
                            logger.severe(locale.getString("plugin-loader.filter.enable.scope", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final InstantiationException e){
                            logger.severe(locale.getString("plugin-loader.filter.enable.abstract", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }catch(final InvocationTargetException | ExceptionInInitializerError e){
                            logger.severe(locale.getString("plugin-loader.filter.enable.internal", file.getName()) + LoggerService.getStackTraceAsString(e));
                        }

                        if(plugin == null) return null;

                        logger.finer(locale.getString("plugin-loader.filter.enable.enable", file.getName()));
                        plugin.onEnable();
                        return plugin;
                    });

                    // run above future
                    try{
                        loaded.add(Objects.requireNonNull(future.get(30, TimeUnit.SECONDS)));
                    }catch(final InterruptedException | TimeoutException | NullPointerException e){
                        logger.severe(locale.getString("plugin-loader.filter.enable.time", file.getName()) + LoggerService.getStackTraceAsString(e));
                    }catch(final Throwable e){
                        logger.severe(locale.getString("plugin-loader.filter.enable.internal", file.getName()) + LoggerService.getStackTraceAsString(e));
                    }finally{
                        future.cancel(true);
                        executor.shutdownNow();
                    }
                }catch(Throwable e){
                    logger.severe(locale.getString("plugin-loader.filter.enable.jar", file.getName()) + LoggerService.getStackTraceAsString(e));
                }
            }
        });

        return loaded;
    }

}
