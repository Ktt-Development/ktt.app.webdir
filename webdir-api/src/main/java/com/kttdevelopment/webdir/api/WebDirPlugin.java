package com.kttdevelopment.webdir.api;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class WebDirPlugin {

    private final PluginService service;

    public WebDirPlugin(final PluginService service){
        this.service = service;
    }

    public final String getPluginName(){
        return service.getPluginName();
    }

    public final Logger getLogger(){
        return service.getLogger();
    }

    public final File getPluginFolder(){
        return service.getPluginFolder();
    }

    public final WebDirPlugin getPlugin(final String pluginName){
        return service.getPlugin(pluginName);
    }

    public final <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass){
        return service.getPlugin(pluginName, pluginClass);
    }

    public final LocaleBundle getLocaleBundle(final String resource){
        return service.getLocaleBundle(resource, getClass().getClassLoader());
    }

    public final File getSourcesFolder(){
        return service.getSourcesFolder();
    }

    public final File getOutputFolder(){
        return service.getOutputFolder();
    }

    public final File getDefaultsFolder(){
        return service.getDefaultsFolder();
    }

    public final File getPluginsFolder(){
        return service.getPluginsFolder();
    }

    private final Map<String,Renderer> renderers = new HashMap<>();

    public synchronized final void addRenderer(final String rendererName, final Renderer renderer){
        renderers.put(Objects.requireNonNull(rendererName), Objects.requireNonNull(renderer));
    }

    public final Map<String,Renderer> getRenderers(){
        return Collections.unmodifiableMap(renderers);
    }

    public void onEnable(){}

    @Override
    public String toString(){
        return "WebDirPlugin{" +
               "service=" + service +
               ", renderers=" + renderers +
               '}';
    }

}
