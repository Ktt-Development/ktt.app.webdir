package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.serviceprovider.*;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.httpserver.SimpleHttpServerUnmodifiable;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.*;

public final class PluginServiceLoader {

    private static final Logger logger = Logger.getLogger("WebDir / PluginService");

    private final Map<WebDirPlugin,List<Formatter>> formatters = new HashMap<>();

    public final Map<WebDirPlugin, List<Formatter>> getFormatters(){
        return Collections.unmodifiableMap(formatters);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    PluginServiceLoader(final File pluginsFolder){
        final String prefix = '[' + locale.getString("pluginService") + ']' + ' ';

        logger.info(prefix + locale.getString("pluginService.init.start"));

        // load jars
        final List<URL> pluginUrls = new ArrayList<>();
        for(final File file : Objects.requireNonNullElse(pluginsFolder.listFiles((dir, name) -> !dir.isDirectory() && name.endsWith(".jar")),new File[0])){
            try{
                pluginUrls.add(file.toURI().toURL());
            }catch(final MalformedURLException e){
                logger.severe(prefix + locale.getString("pluginService.init.badURL") + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }

        // load yml
        final URLClassLoader loader = new URLClassLoader(pluginUrls.toArray(new URL[0]));
        final Enumeration<URL> resources;
        try{
            resources = loader.findResources("plugin.yml");
        }catch(IOException e){
            logger.severe(prefix + locale.getString("pluginService.init.failedYml") + '\n' + LoggerService.getStackTraceAsString(e));
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
                logger.warning(prefix + locale.getString((e instanceof YamlException) ? "pluginService.init.badSyntax" : "pluginService.init.badStream", resource.toString()) + '\n' + LoggerService.getStackTraceAsString(e));
                continue;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        logger.warning(prefix + locale.getString("pluginService.init.stream",resource.toString()) + '\n' + LoggerService.getStackTraceAsString(e));
                    }
            }

            final String main = yml.get("main").toString();
            try{
                plugins.put((Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(main)),yml);
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.warning(prefix + locale.getString("pluginService.init.missingMain",main));
            }catch(final ClassCastException ignored){
                logger.warning(prefix + locale.getString("pluginService.init.badCast"));
            }
        }

        plugins.forEach((pluginClass, yml) -> {
            try{

                final PluginService provider = new PluginService() {

                    private final Logger logger;
                    private final SimpleHttpServer server;
                    private final ConfigurationFile config;
                    private final LocaleBundle locale;
                    private final String pluginName, version;
                    private final List<String> authors, dependencies;
                    private final Class<WebDirPlugin> main;

                    {
                        server = new SimpleHttpServerUnmodifiable(Application.server.getServer());
                        config = new ConfigurationFileImpl();
                        locale = new LocaleBundleImpl();
                        pluginName = Objects.requireNonNull(yml.getString("name"));
                        version = yml.getString("version");
                        authors = yml.getList("authors");
                        dependencies = yml.getList("dependencies");
                        main = pluginClass;

                        logger = Logger.getLogger(pluginName);
                    }

                    @Override
                    public final Logger getLogger(){
                        return logger;
                    }

                    @Override
                    public final SimpleHttpServer getHttpServer(){
                        return server;
                    }

                    @Override
                    public final ConfigurationFile getConfiguration(){
                        return config;
                    }

                    @Override
                    public final LocaleBundle getLocale(){
                        return locale;
                    }

                    @Override
                    public final boolean hasPermission(final String permission){
                        return permissions.getPermissions().hasPermission((InetAddress) null, permission);
                    }

                    @Override
                    public final boolean hasPermission(final InetAddress address, final String permission){
                        return permissions.getPermissions().hasPermission(address, permission);
                    }

                    @Override
                    public final String getPluginName(){
                        return pluginName;
                    }

                    @Override
                    public final String getVersion(){
                        return version;
                    }

                    @Override
                    public final String getAuthor(){
                        return authors.size() >= 1 ? authors.get(0) : null;
                    }

                    @Override
                    public final List<String> getAuthors(){
                        return authors;
                    }

                    @Override
                    public final Class<WebDirPlugin> getMainClass(){
                        return main;
                    }

                    @Override
                    public final List<String> getDependencies(){
                        return dependencies;
                    }
                };

                try{
                    final WebDirPlugin plugin = pluginClass.getDeclaredConstructor(PluginService.class).newInstance(provider);
                    plugin.onEnable();

                    // load methods
                    formatters.put(plugin, plugin.getFormatters());

                    logger.info(locale.getString("pluginService.internal.loaded", provider.getPluginName()));
                }catch(final NullPointerException | NoSuchMethodException e){
                    logger.severe(prefix + locale.getString("pluginService.internal.notFound", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final IllegalAccessException | SecurityException e){
                    logger.severe(prefix + locale.getString("pluginService.internal.scope", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final IllegalArgumentException e){
                    logger.severe(prefix + locale.getString("pluginService.internal.params", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final ExceptionInInitializerError | InstantiationException | InvocationTargetException e){
                    logger.severe(prefix + locale.getString("pluginService.internal.methodException", provider.getPluginName()) + '\n' + LoggerService.getStackTraceAsString(e));
                }
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("pluginService.internal.missingRequired", pluginClass.getSimpleName()));
            }
        });

        logger.info(prefix + locale.getString("pluginService.init.finished"));
    }

}
