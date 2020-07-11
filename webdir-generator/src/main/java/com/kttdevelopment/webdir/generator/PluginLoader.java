package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.object.Tuple3;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;
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

    // Global Settings //

    private static final String mainClassName = "main";

    private static final String pluginDirKey = "plugins_dir", pluginDirDefault = ".plugins";

    private static final String pluginYml = "plugin.yml";

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

        // load plugin main
        final List<Tuple3<File,Class<WebDirPlugin>,ConfigurationSection>> plugins = new ArrayList<>();

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
                plugins.add(new Tuple3<>(plugin, (Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(mainClassName)), yml));
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
                    final Logger pluginLogger = Main.getLoggerService().getLogger(pluginName);

                    try{
                        final WebDirPlugin plugin = mainClass.getDeclaredConstructor(PluginService.class).newInstance(provider);
                        loader.accept(plugin);
                        loadedPlugins.incrementAndGet();
                    }catch(final InstantiationException ignored){
                        pluginLogger.severe(locale.getString("pluginLoader.loader.abstract", pluginName));
                    }catch(final IllegalAccessException ignored){
                        pluginLogger.severe(locale.getString("pluginLoader.loader.scope", pluginName));
                    }catch(final NoSuchMethodException | IllegalArgumentException ignored){
                        pluginLogger.severe(locale.getString("pluginLoader.loader.constArgs", pluginName));
                    }catch(final ExceptionInInitializerError | InvocationTargetException e){
                        pluginLogger.severe(locale.getString("pluginLoader.loader.const", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        pluginLogger.severe(locale.getString("pluginLoader.loader.sec", pluginName) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                }catch(final NullPointerException ignored){
                    Main.getLoggerService().getLogger(pluginFile.getName()).severe(locale.getString("pluginLoader.loader.noName",pluginFile.getName()));
                }
            });

            try{
                future.get(timeout,unit);
            }catch(final Exception e){
                future.cancel(true);

                logger.severe(
                    e instanceof TimeoutException
                    ? locale.getString("pluginLoader.loader.timedOut",pluginFile.getName(),timeout + ' ' + unit.name().toLowerCase())
                    : locale.getString("pluginLoader.loader.unknown",pluginFile.getName()) + '\n' + Exceptions.getStackTraceAsString(e)
                );
            }
        });


        executor.shutdown();
        logger.info(locale.getString("pluginLoader.const.loaded",loadedPlugins.get(),initialPluginCount));
    }
}
