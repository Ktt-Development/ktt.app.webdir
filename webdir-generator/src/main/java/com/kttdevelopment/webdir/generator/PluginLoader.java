package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
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

    private final String pluginsFolder;
    @SuppressWarnings("SpellCheckingInspection")
    private final boolean safemode;

    @SuppressWarnings({"unchecked", "SpellCheckingInspection"})
    public PluginLoader(){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final ConfigService config  = Vars.Main.getConfigService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const"));

        final File pluginsFolder = new File(config.getConfig().getString(Vars.Config.pluginsKey,Vars.Config.defaultPlugins));
        logger.fine(locale.getString("pluginLoader.debug.const.pluginFolder",pluginsFolder.getAbsolutePath()));
        this.pluginsFolder = pluginsFolder.getAbsolutePath();

        if(safemode = Vars.Test.safemode || config.getConfig().getBoolean("safemode")){
            logger.info(locale.getString("pluginLoader.const.skippedReasonSafeMode"));
            return;
        }

    // load files that a jars
        logger.finer(locale.getString("pluginLoader.debug.const.loadJars"));
        final Map<File,URL> pluginsIsJar = new HashMap<>();
        {
            final File[] plugins =  Objects.requireNonNullElse(
                pluginsFolder.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith(".jar")),
                new File[0]
            );
            for(final File file : plugins){
                try{
                    logger.finest(locale.getString("pluginLoader.debug.const.loadJars.plugin",file.getAbsolutePath()));
                    pluginsIsJar.put(file,file.toURI().toURL());
                }catch(final MalformedURLException | IllegalArgumentException e){
                    logger.severe(locale.getString("pluginLoader.const.loadJars.jarMalformedURL", file.getName() + '\n' + Exceptions.getStackTraceAsString(e)));
                }
            }
            logger.finer(locale.getString("pluginLoader.debug.const.loadJars.count",pluginsIsJar.size(),plugins.length));
        }
        final int initialPluginCount = pluginsIsJar.size();

    // load plugins that have a plugin.yml
        logger.finer(locale.getString("pluginLoader.debug.const.loadPluginYML"));
        final Map<File,URL> pluginYMLs = new HashMap<>();
        pluginsIsJar.forEach((file, url) -> {
            logger.finest(locale.getString("pluginLoader.debug.const.loadPluginYML.load",file.getAbsolutePath(),url.getPath()));
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{url})){
                final URL yml = Objects.requireNonNull(loader.findResource(Vars.Plugin.pluginYml));
                pluginYMLs.put(file,yml);
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.classLoaderSecurity", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.missingYML", file.getName()));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.loadPluginYML.classLoaderCloseIO", file.getName()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });
        logger.finer(locale.getString("pluginLoader.debug.const.loadPluginYML.count",pluginYMLs.size(),pluginYMLs.size()-pluginsIsJar.size()));

    // load plugins that have valid plugin.yml, required paramters, and correct main class
        logger.finer(locale.getString("pluginLoader.debug.const.loadValid"));
        final List<PluginLoaderEntry> pluginsValid = new ArrayList<>();
        pluginYMLs.forEach((plugin, ymlURL) -> {
            final String pluginName = plugin.getName();
            logger.finest(locale.getString("pluginLoader.debug.const.loadValid.read",pluginName,ymlURL.getPath()));

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
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.malformedYML", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }catch(final NullPointerException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.missingRequiredKV", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }catch(final IOException e){
                logger.severe(locale.getString("pluginLoader.const.loadValid.openStreamFailed", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                return;
            }finally{
                if(IN != null)
                    try{
                        IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginLoader.const.loadValid.closeStreamFailed", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }

            // test if main class can be loaded
            logger.finest(locale.getString("pluginLoader.debug.const.loadValidMain",pluginName,yml.getString(Vars.Plugin.mainClassKey)));
            try(final URLClassLoader loader = new URLClassLoader(new URL[]{plugin.toURI().toURL()})){
                pluginsValid.add(new PluginLoaderEntry(plugin, (Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(yml.getString(Vars.Plugin.mainClassKey))), yml, pluginYml));
            }catch(final MalformedURLException | IllegalArgumentException e){
                logger.severe(locale.getString("pluginLoader.const.loadJars.jarMalformedURL", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final SecurityException e){
                logger.severe(locale.getString("pluginLoader.const.loadPluginYML.classLoaderSecurity", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }catch(final ClassNotFoundException | NullPointerException e){
                logger.severe(locale.getString("pluginLoader.const.loadValidMain.missingMain", pluginName));
            }catch(final ClassCastException ignored){
                logger.severe(locale.getString("pluginLoader.const.loadValidMain.mainDidNotExtends", pluginName));
            }catch(final IOException e){
                logger.warning(locale.getString("pluginLoader.const.loadPluginYML.classLoaderCloseIO", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });
        logger.finer(locale.getString("pluginLoader.debug.const.loadValid.count",pluginsValid.size(),pluginsValid.size()-pluginYMLs.size()));

        pluginsValid.forEach(entry -> logger.finest(String.format("[%s]: %s", entry.getPluginFile().getAbsolutePath(), entry.getPluginYml().getPluginName())));

    // load plugins with no missing dependencies and no circular dependencies
        logger.finer(locale.getString("pluginLoader.debug.const.loadValidDeps"));
        final List<PluginLoaderEntry> pluginsValidDep = new ArrayList<>();
        pluginsValid.forEach(entry -> {
            logger.finest(locale.getString("pluginLoader.debug.const.loadValidDepsCheck",entry.getPluginYml().getPluginName()));
            // remove dependencies that will be loaded in this for loop
            final List<String> dependencies = new ArrayList<>(Arrays.asList(entry.getPluginYml().getDependencies()));
            pluginsValid.forEach(testPlugin -> dependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!dependencies.isEmpty())
                logger.severe(locale.getString("pluginLoader.const.loadValidDeps.missingDependencies", entry.getPluginYml().getPluginName(), Arrays.toString(entry.getPluginYml().getDependencies())));
            else if(new CircularDependencyChecker(entry,pluginsValid).test(entry))
                logger.severe(locale.getString("pluginLoader.const.loadValidDeps.circularDependency", entry.getPluginYml().getPluginName()));
            else
                pluginsValidDep.add(entry);
        });
        logger.finer(locale.getString("pluginLoader.debug.const.loadValidDeps.count",pluginsValidDep.size(),pluginsValidDep.size()-pluginsValid.size()));

    // sort so dependencies are first
        // load each in given order, and if dependency has not yet loaded in move it to the end of the list'
        logger.finer(locale.getString("pluginLoader.debug.const.sortDeps"));
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
                    (unloadedDependencies.isEmpty() ? pluginsSortedDep : pluginLoadingQueue).add(entry);

                    index++;
                    hasNext = pluginsSortedDep.size() != pluginsToLoad;
                    // iterator can not be used because it does not add to end of list
                }
            logger.finer(locale.getString("pluginLoader.debug.const.sortDeps.loaded"));
        }

    // execute #onEnable for each plugin
        logger.finer(locale.getString("pluginLoader.debug.const.enable"));
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final AtomicInteger loadedPlugins = new AtomicInteger();
        final Iterator<PluginLoaderEntry> iterator = pluginsSortedDep.iterator();
        while(iterator.hasNext()){
            final PluginLoaderEntry entry = iterator.next();
            final String pluginName = entry.getPluginYml().getPluginName();
            logger.finest(locale.getString("pluginLoader.debug.const.enable.plugin",pluginName));
            // check dependencies
            final List<String> missingDependencies = new ArrayList<>(Arrays.asList(entry.getPluginYml().getDependencies()));
            pluginsValid.forEach(testPlugin -> missingDependencies.remove(testPlugin.getPluginYml().getPluginName()));
            if(!missingDependencies.isEmpty()){
                logger.finest(locale.getString("pluginLoader.debug.const.enable.missingDep", entry.getPluginYml().getPluginName(), missingDependencies));
                iterator.remove();
            }else{
                // run main function
                final Future<WebDirPlugin> future = executor.submit(() -> {
                    final PluginService provider = new PluginServiceImpl(entry.getYml(),pluginsFolder);
                    WebDirPlugin plugin = null;
                    try{
                        logger.finest(locale.getString("pluginLoader.debug.const.enable.load",pluginName));
                        plugin = entry.getMainClass().getDeclaredConstructor(PluginService.class).newInstance(provider);
                    }catch(final InstantiationException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.abstract", pluginName));
                    }catch(final IllegalAccessException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.constScope", pluginName));
                    }catch(final NoSuchMethodException | IllegalArgumentException ignored){
                        logger.severe(locale.getString("pluginLoader.const.enable.constArgs", pluginName));
                    }catch(final ExceptionInInitializerError | InvocationTargetException e){
                        logger.severe(locale.getString("pluginLoader.const.enable.constExceptions", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.severe(locale.getString("pluginLoader.const.enable.sec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }

                    if(plugin != null)
                        plugin.onEnable();
                    return plugin;
                });

                try{
                    logger.finest(locale.getString("pluginLoader.debug.const.loader",pluginName));
                    final WebDirPlugin plugin = future.get(Vars.Test.plugin ? 5 : Vars.Plugin.loadTimeout, Vars.Plugin.loadTimeoutUnit);
                    plugin.getRenderers().forEach((rendererName, renderer) -> {
                        renderers.add(new PluginRendererEntry(plugin.getPluginYml().getPluginName(), rendererName, renderer));
                        logger.finest(locale.getString("pluginLoader.debug.const.loader.addRenderer",pluginName,rendererName));
                    });
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
        logger.finer(locale.getString("pluginLoader.debug.const.loader.loaded", loadedPlugins.get(), loadedPlugins.get() - pluginsSortedDep.size()));

        logger.info(locale.getString("pluginLoader.const.loaded",loadedPlugins.get(),initialPluginCount));
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PluginLoader")
            .addObject("pluginsFolder",pluginsFolder)
            .addObject("safe-mode",safemode)
            .addObject("plugins",plugins)
            .addObject("renderers",renderers)
            .toString();
    }

}
