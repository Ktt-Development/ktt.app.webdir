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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PluginLoader {

    // Global Settings //

    private static final String mainClassName = "main";

    private static final String pluginDirKey = "plugins_dir", pluginDirDefault = ".plugins";

    private static final String pluginYml = "plugin.yml";

    private static final int loadTimeout = 30;
    private static final TimeUnit loadTimeoutUnit = TimeUnit.SECONDS;

    // //

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

    protected Consumer<WebDirPlugin> loader = plugin -> {
        plugin.onEnable();
        final String pluginName = plugin.getPluginYml().getPluginName();
        plugin.getRenderers().forEach((rendererName, renderer) -> renderers.add(new PluginRendererEntry(pluginName, rendererName, renderer)));
        plugins.add(plugin);
    };

    @SuppressWarnings({"unchecked", "SpellCheckingInspection"})
    public PluginLoader(){
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const"));

        final File pluginFolder = new File(config.getConfig().getString(pluginDirKey,pluginDirDefault));

        if(config.getConfig().getBoolean("safemode")){
            logger.info(locale.getString("pluginLoader.const.safemode"));
            return;
        }

    // load files that a jars
        final Map<File,URL> pluginsIsJar = new HashMap<>();
        {
            final File[] plugins =  Objects.requireNonNullElse(
                pluginFolder.listFiles((dir, name) -> !dir.isDirectory() && name.toLowerCase().endsWith(".jar")),
                new File[0]
            );
            for(final File file : plugins){
                try{
                    pluginsIsJar.put(file,file.toURI().toURL());
                }catch(final MalformedURLException | IllegalArgumentException e){
                    logger.severe(locale.getString("pluginLoader.const.badURL", file.getName() + '\n' + Exceptions.getStackTraceAsString(e)));
                }
            }
        }
        final int initialPluginCount = pluginsIsJar.size();

    // load plugins that have a plugin.yml
        final Map<File,URL> pluginYMLs = new HashMap<>();
        pluginsIsJar.forEach((file, url) -> {
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL yml = Objects.requireNonNull(loader.findResource(pluginYml));
                pluginYMLs.put(file,yml);
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.const.UCLSec",file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.nullYML",file.getName()));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.UCLCloseIO", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
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
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.loader.noName", pluginName));
                return;
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("pluginLoader.const.badYMLSyntax", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }catch(final IOException e){
                logger.severe(locale.getString("pluginLoader.const.ymlStreamIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }finally{
                if(IN != null)
                    try{
                        IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginLoader.const.streamClose", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }

            // test if main class can be loaded
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{plugin.toURI().toURL()})){
                pluginsValid.add(new PluginLoaderEntry(plugin, (Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(mainClassName)), yml, pluginYml));
            }catch(final MalformedURLException | IllegalArgumentException e){
                logger.severe(locale.getString("pluginLoader.const.UCLSec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.noMainClass", pluginName));
            }catch(final ClassCastException ignored){
                logger.severe(locale.getString("pluginLoader.const.badMainCast", pluginName));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.UCLCloseIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

    // load plugins with no missing dependencies and no circular dependencies
        final List<PluginLoaderEntry> pluginsValidDep = new ArrayList<>();
        pluginsValid.forEach(entry -> {
            // remove dependencies that will be loaded in this for loop
            final List<String> dependencies = Arrays.asList(entry.getPluginYml().getDependencies());
            pluginsValid.forEach(testPlugin -> dependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!dependencies.isEmpty())
                logger.severe(locale.getString("pluginLoader.loader.missingDep",entry.getPluginYml().getPluginName()));
            else if(new HasCircularDependencies(entry,pluginsValid).test(entry))
                logger.severe(locale.getString("pluginLoader.loader.circleDep",entry.getPluginYml().getPluginName()));
            else
                pluginsValidDep.add(entry);
        });

    // sort so dependencies are first
        // load each in given order, and if dependency has not yet loaded in move it to the end of the list
        final List<PluginLoaderEntry> pluginsSortedDep = new ArrayList<>();
        {
            final List<PluginLoaderEntry> pluginLoadingQueue = new ArrayList<>(pluginsValidDep);
            final ListIterator<PluginLoaderEntry> iterator = pluginLoadingQueue.listIterator();
            while(iterator.hasNext()){
                final PluginLoaderEntry entry = iterator.next();
                final List<String> unloadedDependencies = Arrays.asList(entry.getPluginYml().getDependencies());
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
                    iterator.add(entry);
            }
        }

    // execute #onEnable for each plugin
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final AtomicInteger loadedPlugins = new AtomicInteger();
        final Iterator<PluginLoaderEntry> iterator = pluginsSortedDep.iterator();
        while(iterator.hasNext()){
            final PluginLoaderEntry entry = iterator.next();
            // check dependencies
            final List<String> dependencies = Arrays.asList(entry.getPluginYml().getDependencies());
            pluginsValid.forEach(testPlugin -> dependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!dependencies.isEmpty()){
                iterator.remove();
            }else{
                // run main function
                final AtomicBoolean success = new AtomicBoolean(false);
                final Future<?> future = executor.submit(() -> {
                    final PluginService provider = new PluginServiceImpl(entry.getYml());
                    final String pluginName = entry.getPluginYml().getPluginName();
                    WebDirPlugin plugin = null;
                    try{
                        plugin = entry.getMainClass().getDeclaredConstructor(PluginService.class).newInstance(provider);
                    }catch(final InstantiationException ignored){
                        logger.severe(locale.getString("pluginLoader.loader.abstract", pluginName));
                    }catch(final IllegalAccessException ignored){
                        logger.severe(locale.getString("pluginLoader.loader.scope", pluginName));
                    }catch(final NoSuchMethodException | IllegalArgumentException ignored){
                        logger.severe(locale.getString("pluginLoader.loader.constArgs", pluginName));
                    }catch(final ExceptionInInitializerError | InvocationTargetException e){
                        logger.severe(locale.getString("pluginLoader.loader.const", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.severe(locale.getString("pluginLoader.loader.sec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }

                    if(plugin != null){
                        loader.accept(plugin);
                        success.set(true);
                    }else{
                        success.set(false);
                    }
                });

                try{
                    future.get(loadTimeout,loadTimeoutUnit);
                }catch(final Exception e){
                    future.cancel(true);
                    logger.severe(
                        e instanceof TimeoutException
                        ? locale.getString("pluginLoader.loader.timedOut", entry.getPluginYml().getPluginName(), loadTimeout + ' ' + loadTimeoutUnit.name().toLowerCase())
                        : locale.getString("pluginLoader.loader.unknown",entry.getPluginYml().getPluginName()) + '\n' + Exceptions.getStackTraceAsString(e)
                    );
                    success.set(false);
                }
                if(!success.get())
                    iterator.remove();
                else
                    loadedPlugins.incrementAndGet();
            }
        }
        executor.shutdown();

        logger.info(locale.getString("pluginLoader.const.loaded",loadedPlugins.get(),initialPluginCount));
    }

}
