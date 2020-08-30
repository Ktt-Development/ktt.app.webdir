package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.function.IOFilter;
import com.kttdevelopment.webdir.client.plugins.JarClassLoader;
import com.kttdevelopment.webdir.client.plugins.PluginServiceImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class PluginInitializer implements IOFilter<Map<File,PluginYml>,List<WebDirPlugin>> {

    private final File pluginsFolder;

    private final LocaleService locale;
    private final Logger logger;

    public PluginInitializer(final File pluginsFolder){
        this.pluginsFolder = pluginsFolder;
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));
    }

    @Override
    public final List<WebDirPlugin> filter(final Map<File,PluginYml> in){
        final List<WebDirPlugin> loaded = new ArrayList<>();

        in.forEach((file,yml) -> {
            final String pluginName = yml.getPluginName();
            // remove dependency if they loaded
            final List<String> missingDependencies = Arrays.asList(yml.getDependencies());
            missingDependencies.removeIf(name -> {
                for(final WebDirPlugin webDirPlugin : loaded)
                    if(webDirPlugin.getPluginYml().getPluginName().equals(name))
                        return true;
                return false;
            });
            // fail if a dependency failed to load
            if(!missingDependencies.isEmpty()){
                logger.severe(locale.getString("pluginLoader.mainFilter.missingRequired",pluginName,missingDependencies));
            }else{ // load jar
                logger.finest(locale.getString("pluginLoader.mainFilter.classLoader",pluginName));
                // load jar files
                URLClassLoader classLoader = null;
                try{
                    final JarClassLoader loader = new JarClassLoader(file);
                    classLoader = loader.load();
                }catch(ClassNotFoundException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.classNotFound",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final MalformedURLException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.malformedURL",file) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final SecurityException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.accessDenied",file) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final IOException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.failedClassLoader",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                }

                if(classLoader == null) return;

                // execute main method
                final URLClassLoader loader = classLoader;
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                final Future<WebDirPlugin> future = executor.submit(() -> {
                    logger.finest(locale.getString("pluginLoader.mainFilter.loadMain",pluginName));
                    WebDirPlugin plugin = null;
                    { // initialize main class
                        try{
                            plugin = (WebDirPlugin) loader.loadClass(Objects.requireNonNull(yml.getConfiguration().getString("main"))).getDeclaredConstructor(PluginService.class).newInstance(new PluginServiceImpl(yml,pluginsFolder));
                        }catch(final ClassCastException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.classCast",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final NullPointerException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.missingMain",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final ClassNotFoundException e){ // getDeclaredConstructor
                            logger.severe(locale.getString("pluginLoader.mainFilter.missingClass",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final NoSuchMethodException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.missingMethod",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final SecurityException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.accessDeniedMain",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final IllegalAccessException e){ // newInstance
                            logger.severe(locale.getString("pluginLoader.mainFilter.illegalAccess",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final IllegalArgumentException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.illegalArgs",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final InstantiationException e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.illegalScope",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }catch(final InvocationTargetException | ExceptionInInitializerError e){
                            logger.severe(locale.getString("pluginLoader.mainFilter.exception",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                        }
                    }

                    if(plugin == null) return null;

                    logger.finest(locale.getString("pluginLoader.mainFilter.enable",pluginName));
                    plugin.onEnable();
                    return plugin;
                });
                // run above future
                try{
                    loaded.add(Objects.requireNonNull(future.get(30,TimeUnit.SECONDS)));
                }catch(InterruptedException | TimeoutException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.timedOut",pluginName,30 + " " + TimeUnit.SECONDS.name().toLowerCase()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final NullPointerException e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.nullPlugin",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final Throwable e){
                    logger.severe(locale.getString("pluginLoader.mainFilter.enableException",pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                }finally{
                    future.cancel(true);
                    executor.shutdownNow();
                }
            }
        });
        return loaded;
    }

}
