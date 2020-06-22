package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.pluginservice.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public final class PluginServiceLoader {

    private final PluginLibrary library = new PluginLibrary();

    public final PluginLibrary getLibrary(){
        return library;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    PluginServiceLoader(final File pluginsFolder){
        final LocaleService locale = Application.getLocaleService();
        final ConfigService config = Application.getConfigService();
        final Logger logger = Logger.getLogger(locale.getString("pluginService"));
        logger.info(locale.getString("pluginService.init.start"));

        if(config.getConfig().getBoolean("safemode", false)){
            logger.info(locale.getString("pluginService.init.skip"));
            return;
        }

        // load jar files
        final List<URL> pluginUrls = new ArrayList<>();
        {
            for(final File file : Objects.requireNonNullElse(pluginsFolder.listFiles((dir, name) -> !dir.isDirectory() && name.endsWith(".jar")),new File[0])){
                try{
                    pluginUrls.add(file.toURI().toURL());
                }catch(final MalformedURLException e){
                    logger.severe(locale.getString("pluginService.init.badURL") + '\n' + LoggerService.getStackTraceAsString(e));
                }
            }
        }

        // load yml
        final Enumeration<URL> resources;
        final URLClassLoader loader = new URLClassLoader(pluginUrls.toArray(new URL[0]));
        {
            try{
                resources = loader.findResources("plugin.yml");
            }catch(IOException e){
                logger.severe(locale.getString("pluginService.init.failedYml") + '\n' + LoggerService.getStackTraceAsString(e));
                return;
            }
        }

        // load yml
        final Map<Class<WebDirPlugin>, ConfigurationSection> plugins = new LinkedHashMap<>();
        while(resources.hasMoreElements()){
            final URL resource = resources.nextElement();
            final ConfigurationSection yml;

            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(resource.openStream()));
                yml = new ConfigurationSectionImpl((Map) IN.read());
            }catch(final ClassCastException | IOException e){
                logger.warning(locale.getString((e instanceof YamlException) ? "pluginService.init.badSyntax" : "pluginService.init.badStream", resource.toString()) + '\n' + LoggerService.getStackTraceAsString(e));
                continue;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginService.init.stream",resource.toString()) + '\n' + LoggerService.getStackTraceAsString(e));
                    }
            }

            final String main = yml.get("main").toString();
            try{
                plugins.put((Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(main)),yml);
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.warning(locale.getString("pluginService.init.missingMain",main));
            }catch(final ClassCastException ignored){
                logger.warning(locale.getString("pluginService.init.badCast"));
            }
        }

        // initialize plugins
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        plugins.forEach((pluginClass, yml) -> {
            final Future<?> future = executor.submit(() -> {
                try{
                    final PluginService provider = new PluginServiceImpl(pluginClass, yml);
                    final String pluginName = provider.getPluginName();

                    try{
                        final WebDirPlugin plugin = pluginClass.getDeclaredConstructor(PluginService.class).newInstance(provider);
                        plugin.onEnable();
                        library.addPlugin(plugin);

                        logger.info(locale.getString("pluginService.internal.loaded",pluginName));
                    }catch(final
                        ExceptionInInitializerError | InstantiationException | InvocationTargetException |
                        NullPointerException | NoSuchMethodException |
                        IllegalArgumentException |
                        IllegalAccessException | SecurityException e
                    ){
                        final String err;
                        if(e instanceof InvocationTargetException) // ExInit | InstantEx
                            err = "pluginService.internal.methodException";
                        else if(e instanceof NoSuchMethodException) // NPE
                            err = "pluginService.internal.notFound";
                        else if(e instanceof IllegalArgumentException)
                            err = "pluginService.internal.params";
                        else // IllAccess | SecEx
                            err = "pluginService.internal.scope";
                        logger.severe(locale.getString(err, pluginName) + '\n' + LoggerService.getStackTraceAsString(e));
                    }
                }catch(final NullPointerException ignored){
                    logger.severe(locale.getString("pluginService.internal.missingRequired",pluginClass.getSimpleName()));
                }
            });

            try{ // this executes the above runnable
                future.get(30,TimeUnit.SECONDS);
            }catch(InterruptedException | TimeoutException | ExecutionException e){
                future.cancel(true);
                logger.severe(locale.getString(e instanceof TimeoutException ? "pluginService.internal.timeout" : "pluginService.internal.unknown",pluginClass.getSimpleName()));
            }
        });
        executor.shutdown();
        logger.info(locale.getString("pluginService.init.finished"));
    }

}
