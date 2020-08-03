package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class allows you to add custom renderers to the WebDir-Generator and WebDir-Server applications.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public class WebDirPlugin {

    private final PluginService service;

    /**
     * Instantiates a WebDirPlugin. <b>Do not override this method</b>, use {@link #onEnable()} instead.
     *
     * @param service plugin service provider
     *
     * @see #onEnable()
     * @since 01.00.00
     * @author Ktt Development
     */
    public WebDirPlugin(final PluginService service){
        this.service = service;
    }

    /**
     * Returns the logger for the plugin. <b>Do not create your own logger</b>, it will not log to files.
     *
     * @return logger
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public final Logger getLogger(){
        return service.getLogger();
    }

    // plugin

    /**
     * Returns the plugin folder for the plugin. Storing files in the plugins folder is not recommended because plugins may have conflicting files. This folder is exclusive to your plugin only.
     *
     * @return plugin folder
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public final File getPluginFolder(){
        return service.getPluginFolder();
    }

    /**
     * Returns the plugin.yml as an object.
     *
     * @return plugin.yml
     *
     * @see PluginYml
     * @since 01.00.00
     * @author Ktt Development
     */
    public final PluginYml getPluginYml(){
        return service.getPluginYml();
    }

    /**
     * Returns a plugin from the server or null if it is not found. This method can be used to access features of other plugins (however it must be casted).
     *
     * @param pluginName name of the plugin
     * @return plugin
     *
     * @see #getPlugin(String, Class)
     * @since 01.00.00
     * @author Ktt Development
     */
    public final WebDirPlugin getPlugin(final String pluginName){
        return service.getPlugin(pluginName);
    }

    /**
     * Returns a casted plugin from the server or null if it is not found. This method can be used to access features of other plugins.
     *
     * @param pluginName name of the plugin
     * @param <T> plugin class type
     * @param pluginClass plugin class
     * @return casted plugin
     * @throws ClassCastException plugin does not extend class provided
     *
     * @see #getPlugin(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return service.getPlugin(pluginName,pluginClass);
    }

    // resources

    /**
     * Returns an input stream for a file in the resources folder (same as {@link ClassLoader#getResourceAsStream(String)}.
     *
     * @param path path to resource
     * @return resource as stream
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public final InputStream getResource(final String path){
        return service.getResource(path);
    }

    /**
     * Creates and loads a configuration from a file.
     *
     * @param file configuration file
     * @return configuration
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public final ConfigurationSection createConfiguration(final File file){
        return service.createConfiguration(file);
    }

    /**
     * Creates and loads a configuration from an input stream.
     *
     * @param stream input stream
     * @return configuration
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public final ConfigurationSection createConfiguration(final InputStream stream){
        return service.createConfiguration(stream);
    }

    /**
     * Returns a locale bundle for a resource prefix. See {@link LocaleBundle} for more details on how to use this.
     *
     * @param resource locale bundle resource prefix
     * @return locale bundle
     *
     * @see LocaleBundle
     * @since 01.00.00
     * @author Ktt Development
     */
    public final LocaleBundle getLocaleBundle(final String resource){
        return service.getLocaleBundle(resource);
    }

    // impl

    private final Map<String,Renderer> renderers = new HashMap<>();

    /**
     * Adds a renderer to the plugin.
     *
     * @param rendererName name of the renderer
     * @param renderer renderer
     *
     * @see Renderer
     * @since 01.00.00
     * @author Ktt Development
     */
    public synchronized final void addRenderer(final String rendererName, final Renderer renderer){
        renderers.put(Objects.requireNonNull(rendererName),Objects.requireNonNull(renderer));
    }

    /**
     * Returns a map of renderers currently added to the plugin.
     *
     * @return map of renderers
     *
     * @see Renderer
     * @since 01.00.00
     * @author Ktt Development
     */
    public final Map<String,Renderer> getRenderers(){
        return Collections.unmodifiableMap(renderers);
    }

    // impl

    /**
     * Runs when the plugin is first loaded.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    @SuppressWarnings("EmptyMethod")
    public void onEnable(){}

    //

    @Override
    public String toString(){
        return "WebDirPlugin{" +
               "pluginService=" + service +
               ", renderers=" + renderers +
               '}';
    }

}
