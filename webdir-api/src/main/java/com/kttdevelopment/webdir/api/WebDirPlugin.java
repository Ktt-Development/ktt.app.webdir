package com.kttdevelopment.webdir.api;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class allows you to add custom renders to WebDir.
 *
 * @since 1.0.0
 * @version 1.0.0
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
     * @since 1.0.0
     * @author Ktt Development
     */
    public WebDirPlugin(final PluginService service){
        this.service = service;
    }

    /**
     * Returns the name of the plugin from the plugin.yml file.
     *
     * @return plugin name
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public final String getPluginName(){
        return service.getPluginName();
    }

    /**
     * Returns the dedicated logger for the plugin. <b>Do not create your own logger</b>, it will not log to files.
     *
     * @return logger
     *
     * @see Logger
     * @since 1.0.0
     * @author Ktt Development
     */
    public final Logger getLogger(){
        return service.getLogger();
    }

    /**
     * Returns the dedicated plugin folder for the plugin. Storing files in the plugins folder is not recommended because plugins may have conflicting files. This folder is exclusive to your plugin only.
     *
     * @return plugin folder
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public final File getPluginFolder(){
        return service.getPluginFolder();
    }

    /**
     * Returns a plugin from the server or null if it is not found. This method can be used to access the features of another plugin.
     *
     * @param pluginName name of the plugin
     * @return plugin
     *
     * @see #getPlugin(String, Class)
     * @since 1.0.0
     * @author Ktt Development
     */
    public final WebDirPlugin getPlugin(final String pluginName){
        return service.getPlugin(pluginName);
    }

    /**
     * Returns a casted plugin from the server or null if it can not be found. This method can be used to access the features of another plugin.
     *
     * @param pluginName name of the plugin
     * @param pluginClass plugin type
     * @param <T> plugin class type
     * @return casted plugin
     * @throws ClassCastException plugin does not extend class provided
     *
     * @see #getPlugin(String, Class)
     * @since 1.0.0
     * @author Ktt Development
     */
    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return service.getPlugin(pluginName, pluginClass);
    }

    /**
     * Returns a locale bundle fro a resource prefix. See {@link LocaleBundle} for more details on how to use this.
     *
     * @param resource resource prefix
     * @return locale bundle
     *
     * @see LocaleBundle
     * @since 1.0.0
     * @author Ktt Development
     */
    public final LocaleBundle getLocaleBundle(final String resource){
        return service.getLocaleBundle(resource, getClass().getClassLoader());
    }

    /**
     * Returns the sources folder.
     *
     * @return sources folder
     *
     * @see #getOutputFolder()
     * @see #getDefaultsFolder()
     * @since 1.0.0
     * @author Ktt Development
     */
    public final File getSourcesFolder(){
        return service.getSourcesFolder();
    }

    /**
     * Returns the output folder.
     *
     * @return output folder
     *
     * @see #getSourcesFolder()
     * @see #getDefaultsFolder()
     * @since 1.0.0
     * @author Ktt Development
     */
    public final File getOutputFolder(){
        return service.getOutputFolder();
    }

    /**
     * Returns the defaults folder.
     *
     * @return defaults folder
     *
     * @see #getSourcesFolder()
     * @see #getOutputFolder()
     * @since 1.0.0
     * @author Ktt Development
     */
    public final File getDefaultsFolder(){
        return service.getDefaultsFolder();
    }

    /**
     * Returns the plugins folder.
     *
     * @return plugins golder
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public final File getPluginsFolder(){
        return service.getPluginsFolder();
    }

    private final Map<String,Renderer> renderers = new HashMap<>();

    /**
     * Adds a renderer to the plugin.
     *
     * @param rendererName name of the renderer
     * @param renderer renderer
     *
     * @see Renderer
     * @since 1.0.0
     * @author Ktt Development
     */
    public synchronized final void addRenderer(final String rendererName, final Renderer renderer){
        renderers.put(Objects.requireNonNull(rendererName), Objects.requireNonNull(renderer));
    }

    /**
     * Returns a map of the renderers currently added to the plugin.
     *
     * @return map of renderers
     *
     * @see Renderer
     * @since 1.0.0
     * @author Ktt Development
     */
    public final Map<String,Renderer> getRenderers(){
        return Collections.unmodifiableMap(renderers);
    }

    /**
     * Runs when the plugin is first loaded.
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public void onEnable(){}

    @Override
    public String toString(){
        return "WebDirPlugin{" +
               "service=" + service +
               ", renderers=" + renderers +
               '}';
    }

}
