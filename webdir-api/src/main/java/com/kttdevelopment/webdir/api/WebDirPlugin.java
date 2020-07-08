package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class WebDirPlugin {

    private final PluginService service;

    public WebDirPlugin(final PluginService service){
        this.service = service;
    }

    public final Logger getLogger(){
        return service.getLogger();
    }

    // plugin

    public final File getPluginFolder(){
        return service.getPluginFolder();
    }

    public final PluginYml getPluginYml(){
        return service.getPluginYml();
    }

    // resources

    public final InputStream getResource(final String path){
        return service.getResource(path);
    }

    public final ConfigurationFile createConfiguration(final File file){
        return service.createConfiguration(file);
    }

    public final LocaleBundle getLocaleBundle(final String resource){
        return service.getLocaleBundle(resource);
    }

    // impl

    private final Map<String,Renderer> renderers = new HashMap<>();

    public synchronized final void addRenderer(final String rendererName, final Renderer renderer){
        renderers.put(Objects.requireNonNull(rendererName),Objects.requireNonNull(renderer));
    }

    public final Map<String,Renderer> getRenderers(){
        return Collections.unmodifiableMap(renderers);
    }

    // impl

    @SuppressWarnings("EmptyMethod")
    public void onEnable(){}

    @SuppressWarnings("EmptyMethod")
    public void onDisable(){}

}
