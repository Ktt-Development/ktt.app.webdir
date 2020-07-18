package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class PluginLoader {

    private final List<PluginRendererEntry> renderers = new ArrayList<>();

    public final List<PluginRendererEntry> getRenderers(){
        return Collections.unmodifiableList(renderers);
    }

    private final List<WebDirPlugin> plugins = new ArrayList<>();

    public final List<WebDirPlugin> getPlugins(){
        return Collections.unmodifiableList(plugins);
    }

    //

    public final WebDirPlugin getPlugin(final String pluginName){
        for(final WebDirPlugin plugin : plugins)
            if(plugin.getPluginYml().getPluginName().equals(pluginName))
                return plugin;
        return null;
    }

    @SuppressWarnings({"unchecked", "unused"}) // IntelliJ defect; doesn't recognize plugin class param is required for casting
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return (T) getPlugin(pluginName);
    }

    //

    @SuppressWarnings({"unchecked", "SpellCheckingInspection"})
    public PluginLoader(){
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const"));

        final File pluginsFolder = new File(config.getConfig().getString(Vars.Config.pluginsKey,Vars.Config.defaultPlugins));

        if(Vars.Test.safemode || config.getConfig().getBoolean("safemode")){
            logger.info(locale.getString("pluginLoader.const.safemode"));
            return;
        }

    // load files that a jars
        final Map<File,URL> pluginsIsJar = new HashMap<>();
        {
            final File[] plugins =  Objects.requireNonNullElse(
                pluginsFolder.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith(".jar")),
                new File[0]
            );
            for(final File file : plugins){
                try{
                    pluginsIsJar.put(file,file.toURI().toURL());
                }catch(final MalformedURLException | IllegalArgumentException e){
                    logger.severe(locale.getString("pluginLoader.const.loadJars.badURL", file.getName() + '\n' + Exceptions.getStackTraceAsString(e)));
                }
            }
        }
        final int initialPluginCount = pluginsIsJar.size();

    // load plugins that have a plugin.yml
        final Map<File,URL> pluginYMLs = new HashMap<>();
        pluginsIsJar.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL yml = Objects.requireNonNull(loader.findResource(Vars.Plugin.pluginYml));
                pluginYMLs.put(file,yml);
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.UCLSec", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.null", file.getName()));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.loadPluginYML.closeIO", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

    // load plugins that have valid plugin.yml, required paramters, and correct main class
        final List<PluginLoaderEntry> pluginsValid = new ArrayList<>();
        pluginYMLs.forEach((plugin, ymlURL) -> {
            final String pluginName = plugin.getName();

            final ConfigurationSection yml;
            final PluginYml pluginYml;

            // read plugin yml
            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(ymlURL.openStream()));
                //noinspection rawtypes
                yml = new ConfigurationSectionImpl((Map) IN.read());
                // test if yml has all required keys
                pluginYml = new PluginYmlImpl(yml);
            }catch(final NullPointerException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.missingReq", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.malformedYML", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }catch(final IOException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.openIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }finally{
                if(IN != null)
                    try{
                        IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginLoader.const.loadValid.closeIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }

            // test if main class can be loaded
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{plugin.toURI().toURL()})){
                pluginsValid.add(new PluginLoaderEntry(plugin, (Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(yml.getString(Vars.Plugin.mainClassKey))), yml, pluginYml));
            }catch(final MalformedURLException | IllegalArgumentException e){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.UCLSec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final ClassNotFoundException | NullPointerException e){
                logger.severe(locale.getString("pluginLoader.const.loadValidMain.notFound", pluginName));
            }catch(final ClassCastException ignored){
                logger.severe(locale.getString("pluginLoader.const.loadValidMain.badCast", pluginName));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.loadPluginYML.closeIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

    // load plugins with no missing dependencies and no circular dependencies
        final List<PluginLoaderEntry> pluginsValidDep = new ArrayList<>();
        pluginsValid.forEach(entry -> {
            // remove dependencies that will be loaded in this for loop
            final List<String> dependencies = new ArrayList<>(Arrays.asList(entry.getPluginYml().getDependencies()));
            pluginsValid.forEach(testPlugin -> dependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!dependencies.isEmpty())
                logger.severe(locale.getString("pluginLoader.const.loadValidDeps.missingDep", entry.getPluginYml().getPluginName(), Arrays.toString(entry.getPluginYml().getDependencies())));
            else if(new CircularDependencyChecker(entry,pluginsValid).test(entry))
                logger.severe(locale.getString("pluginLoader.const.loadValidDeps.circleDep", entry.getPluginYml().getPluginName()));
            else
                pluginsValidDep.add(entry);
        });

    // sort so dependencies are first
        // load each in given order, and if dependency has not yet loaded in move it to the end of the list
        final List<PluginLoaderEntry> pluginsSortedDep = new ArrayList<>();
        {
            final int pluginsToLoad = pluginsValidDep.size();
            final List<PluginLoaderEntry> pluginLoadingQueue = new ArrayList<>(pluginsValidDep);
            boolean hasNext = true;

            int index = 0;

            if(!pluginLoadingQueue.isEmpty())
                while(hasNext){
                    final PluginLoaderEntry entry = pluginLoadingQueue.get(index);
                    final List<String> unloadedDependencies = new ArrayList<>(Arrays.asList(entry.getPluginYml().getDependencies()));
                    unloadedDependencies.removeIf(dependencyName -> {
                        // remove dependencies if they have alreay been read by this loop
                        for(final PluginLoaderEntry dependency : pluginsSortedDep)
                            if(dependency.getPluginYml().getPluginName().equals(dependencyName))
                                return true;
                        return false;
                    });
                    // if all required dependencies have already been read add to loading
                    // else add to end of queue to try again
                    if(unloadedDependencies.isEmpty())
                        pluginsSortedDep.add(entry);
                    else
                        pluginLoadingQueue.add(entry);
                    index++;
                    hasNext = pluginsSortedDep.size() != pluginsToLoad;
                }
        }

    // execute #onEnable for each plugin
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final AtomicInteger loadedPlugins = new AtomicInteger();
        final Iterator<PluginLoaderEntry> iterator = pluginsSortedDep.iterator();
        while(iterator.hasNext()){
            final PluginLoaderEntry entry = iterator.next();
            // check dependencies
            final List<String> dependencies = new ArrayList<>(Arrays.asList(entry.getPluginYml().getDependencies()));
            pluginsValid.forEach(testPlugin -> dependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!dependencies.isEmpty()){
                iterator.remove();
            }else{
                // run main function
                final Future<WebDirPlugin> future = executor.submit(() -> {
                    final PluginService provider = new PluginServiceImpl(entry.getYml(),pluginsFolder);
                    final String pluginName = entry.getPluginYml().getPluginName();
                    WebDirPlugin plugin = null;
                    try{
                        plugin = entry.getMainClass().getDeclaredConstructor(PluginService.class).newInstance(provider);
                    }catch(final InstantiationException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.abstract", pluginName));
                    }catch(final IllegalAccessException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.scope", pluginName));
                    }catch(final NoSuchMethodException | IllegalArgumentException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.constArgs", pluginName));
                    }catch(final ExceptionInInitializerError | InvocationTargetException e){
                        logger.severe(locale.getString("pluginLoader.const.enable.const", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.severe(locale.getString("pluginLoader.const.enable.sec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }

                    if(plugin != null){
                        plugin.onEnable();
                        return plugin;
                    }
                    return null;
                });

                try{
                    final WebDirPlugin plugin = future.get(Vars.Plugin.loadTimeout, Vars.Plugin.loadTimeoutUnit);
                    plugin.getRenderers().forEach((rendererName, renderer) -> renderers.add(new PluginRendererEntry(plugin.getPluginYml().getPluginName(), rendererName, renderer)));
                    plugins.add(plugin);
                    loadedPlugins.incrementAndGet();
                }catch(final Throwable e){
                    future.cancel(true);
                    logger.severe(
                        e instanceof TimeoutException
                        ? locale.getString("pluginLoader.const.loader.timedOut", entry.getPluginYml().getPluginName(), Vars.Plugin.loadTimeout + " " + Vars.Plugin.loadTimeoutUnit.name().toLowerCase())
                        : locale.getString("pluginLoader.const.loader.uncaught", entry.getPluginYml().getPluginName()) + '\n' + Exceptions.getStackTraceAsString(e)
                    );
                    iterator.remove();
                }
            }
        }

        logger.info(locale.getString("pluginLoader.const.loaded",loadedPlugins.get(),initialPluginCount));
    }

}
