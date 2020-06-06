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
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.*;

@SuppressWarnings("SpellCheckingInspection")
public final class PluginServiceLoader {

    private final List<PluginFormatter> formatters = new LinkedList<>();

    public final List<PluginFormatter> getFormatters(){
        return Collections.unmodifiableList(formatters);
    }

    private final List<PluginHandler> handlers = new LinkedList<>();

    public final List<PluginHandler> getHandlers(){
        return Collections.unmodifiableList(handlers);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    PluginServiceLoader(final File pluginsFolder){
        final Logger logger = Logger.getLogger(locale.getString("pluginService"));
        logger.info(locale.getString("pluginService.init.start"));

        if(config.getConfig().getBoolean("safemode", false)){
            logger.info(locale.getString("pluginService.init.skip"));
            return;
        }

        // load jars
        final List<URL> pluginUrls = new ArrayList<>();
        for(final File file : Objects.requireNonNullElse(pluginsFolder.listFiles((dir, name) -> !dir.isDirectory() && name.endsWith(".jar")),new File[0])){
            try{
                pluginUrls.add(file.toURI().toURL());
            }catch(final MalformedURLException e){
                logger.severe(locale.getString("pluginService.init.badURL") + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }

        // load yml
        final URLClassLoader loader = new URLClassLoader(pluginUrls.toArray(new URL[0]));
        final Enumeration<URL> resources;
        try{
            resources = loader.findResources("plugin.yml");
        }catch(IOException e){
            logger.severe(locale.getString("pluginService.init.failedYml") + '\n' + LoggerService.getStackTraceAsString(e));
            return;
        }

        // load plugins
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

        plugins.forEach((pluginClass, yml) -> {
            try{
                final PluginService provider = new PluginServiceImpl(pluginClass,yml);

                try{
                    final WebDirPlugin plugin = pluginClass.getDeclaredConstructor(PluginService.class).newInstance(provider);
                    plugin.onEnable();

                    // load methods
                    plugin.getFormatters().forEach((formatter) -> formatters.add(new PluginFormatter(plugin, formatter)));
                    plugin.getHandlers().forEach((handler) -> handlers.add(new PluginHandler(plugin,handler)));

                    logger.info(locale.getString("pluginService.internal.loaded", provider.getPluginName()));
                }catch(final NullPointerException | NoSuchMethodException e){
                    logger.severe(locale.getString("pluginService.internal.notFound", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final IllegalAccessException | SecurityException e){
                    logger.severe(locale.getString("pluginService.internal.scope", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final IllegalArgumentException e){
                    logger.severe(locale.getString("pluginService.internal.params", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final ExceptionInInitializerError | InstantiationException | InvocationTargetException e){
                    logger.severe(locale.getString("pluginService.internal.methodException", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginService.internal.missingRequired", pluginClass.getSimpleName()));
            }
        });

        logger.info(locale.getString("pluginService.init.finished"));
    }

}
