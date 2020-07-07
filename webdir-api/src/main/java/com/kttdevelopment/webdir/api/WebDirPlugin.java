package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

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

        // todo: locale

    // impl

    private final List<Renderer> renderers = new ArrayList<>();

    public synchronized final void addRenderer(final Renderer renderer){
        renderers.add(renderer);
    }

    public final Renderer[] getRenderers(){
        return Collections.unmodifiableList(renderers).toArray(new Renderer[0]);
    }

    // impl

    public void onEnable(){}

    public void onDisable(){}

}
