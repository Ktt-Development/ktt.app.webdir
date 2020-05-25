package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.extension.Extension;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.page.Page;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

public final class PluginService {

    private final List<Extension> extensions = new ArrayList<>();
    private final List<Formatter> formatters = new ArrayList<>();
    private final List<Page> pages = new ArrayList<>();

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
        plugins.forEach(plugin -> {
            final String pluginName = plugin.getCanonicalName();
            try{
                plugin.getMethod("onEnable").invoke(null);

                // load get methods
                try{
                    extensions.addAll((List<Extension>) plugin.getMethod("getExtensions").invoke(null));
                }catch(final Exception e){
                    handleException(pluginName,"getExtensions()",e);
                }

                try{
                    formatters.addAll((List<Formatter>) plugin.getMethod("getFormatters").invoke(null));
                }catch(final Exception e){
                    handleException(pluginName,"getFormatters()",e);
                }

                try{
                    pages.addAll((List<Page>) plugin.getMethod("getPages").invoke(null));
                }catch(final Exception e){
                    handleException(pluginName,"getPages()",e);
                }

                logger.info(prefix + locale.getString("pluginService.internal.loaded"));
            }catch(final Exception e){
                handleException(pluginName,"onEnable()",e);
            }
        });

        logger.info(prefix + locale.getString("pluginService.init.finished"));
    }

    private void handleException(final String plugin, final String method, final Exception e){
        final String prefix = '[' + locale.getString("pluginService") + ']' + ' ';
        final String err;
        if(e instanceof IllegalAccessException)
            err = "pluginService.internal.loaded";
        else if(e instanceof IllegalArgumentException)
            err = "pluginService.internal.scope";
        else if(e instanceof NullPointerException || e instanceof NoSuchMethodException)
            err = "pluginService.internal.notFound";
        else if(e instanceof InvocationTargetException)
            err = "pluginService.internal.methodException";
        else
            err = "pluginService.internal.exception";
        logger.warning(prefix + locale.getString(err,plugin,method) + '\n' + Logger.getStackTraceAsString(e));
    }

}
