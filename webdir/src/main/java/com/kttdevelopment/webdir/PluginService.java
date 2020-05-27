package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.PluginServiceProvider;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.extension.Extension;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.httpserver.SimpleHttpServerUnmodifiable;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

public final class PluginService {

    private final List<Extension> extensions = new ArrayList<>();
    private final List<Formatter> formatters = new ArrayList<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    PluginService(final File pluginsFolder){
        final String prefix = '[' + locale.getString("pluginService") + ']' + ' ';

        logger.info(prefix + locale.getString("pluginService.init.start"));

        // load jars
        final List<URL> pluginUrls = new ArrayList<>();
        for(final File file : pluginsFolder.listFiles((dir, name) -> !dir.isDirectory() && name.endsWith(".jar"))){
            try{
                pluginUrls.add(file.toURI().toURL());
            }catch(final MalformedURLException e){
                logger.severe(prefix + locale.getString("pluginService.init.badURL") + '\n' + Logger.getStackTraceAsString(e));
            }
        }

        // load yml
        final URLClassLoader loader = new URLClassLoader(pluginUrls.toArray(new URL[0]));
        final Enumeration<URL> resources;
        try{
            resources = loader.findResources("plugin.yml");
        }catch(IOException e){
            logger.severe(prefix + locale.getString("pluginService.init.failedYml") + '\n' + Logger.getStackTraceAsString(e));
            return;
        }

        // load plugins
        final List<Class<WebDirPlugin>> plugins = new ArrayList<>();
        while(resources.hasMoreElements()){
            final URL resource = resources.nextElement();
            final Map yml;

            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(resource.openStream()));
                yml = (Map) IN.read();
            }catch(final ClassCastException | IOException e){
                logger.warning(prefix + locale.getString((e instanceof YamlException) ? "pluginService.init.badSyntax" : "pluginService.init.badStream", resource.toString()) + '\n' + Logger.getStackTraceAsString(e));
                continue;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        logger.warning(prefix + locale.getString("pluginService.init.stream",resource.toString()) + '\n' + Logger.getStackTraceAsString(e));
                    }
            }

            final String main = yml.get("main").toString();
            try{
                plugins.add((Class<WebDirPlugin>) loader.loadClass(Objects.requireNonNull(main)));
            }catch(final ClassNotFoundException | NullPointerException ignored){
                logger.warning(prefix + locale.getString("pluginService.init.missingMain",main));
            }catch(final ClassCastException ignored){
                logger.warning(prefix + locale.getString("pluginService.init.badCast"));
            }
        }

        // start plugins
        plugins.forEach(pl -> {
            final String name = pl.getSimpleName();
            try{
                // each plugin only has permission to use its own provider
                final PluginServiceProvider provider = new PluginServiceProvider() {

                    private final ConfigurationFile config = new ConfigurationFileImpl();
                    private final LocaleBundle locale = new LocaleBundleImpl();

                    @Override
                    public final SimpleHttpServer getHttpServer(){
                        return new SimpleHttpServerUnmodifiable(server.getServer());
                    }

                    // local config

                    @Override
                    public final ConfigurationFile getConfiguration(){
                        return config;
                    }

                    // locale locale

                    @Override
                    public final LocaleBundle getLocale(){
                        return locale;
                    }

                    // local permission // todo: change so only has access to local permissions

                    @Override
                    public final boolean hasPermission(final String permission){
                        return hasPermission(null,permission);
                    }


                    @Override
                    public final boolean hasPermission(final InetAddress address, final String permission){
                        return permissions.getPermissions().hasPermission(address,permission);
                    }
                };

                final WebDirPlugin plugin = pl.getDeclaredConstructor(PluginServiceProvider.class).newInstance(provider);

                plugin.onEnable();

                // load get methods
                extensions.addAll(plugin.getExtensions());
                formatters.addAll(plugin.getFormatters());

                logger.info(prefix + locale.getString("pluginService.internal.loaded",name));
            }catch(final NullPointerException |  NoSuchMethodException e){
                logger.severe(prefix + locale.getString("pluginService.internal.notFound",name) + '\n' + Logger.getStackTraceAsString(e));
            }catch(final IllegalAccessException | SecurityException e){
                logger.severe(prefix + locale.getString("pluginService.internal.scope",name) + '\n' + Logger.getStackTraceAsString(e));
            }catch(final IllegalArgumentException e){
                logger.severe(prefix + locale.getString("pluginService.internal.params",name) + '\n' + Logger.getStackTraceAsString(e));
            }catch(final ExceptionInInitializerError |  InstantiationException | InvocationTargetException e){
                logger.severe(prefix + locale.getString("pluginService.internal.methodException",name) + '\n' + Logger.getStackTraceAsString(e));
            }
        });

        logger.info(prefix + locale.getString("pluginService.init.finished"));
    }

}
