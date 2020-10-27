package com.kttdevelopment.webdir.client.plugin;

import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

public class PluginRendererEntry {

    private final String pluginName, rendererName;
    private final Renderer renderer;

    public PluginRendererEntry(final String pluginName, final String rendererName, final Renderer renderer){
        this.pluginName   = pluginName;
        this.rendererName = rendererName;
        this.renderer     = renderer;
    }

    public final String getPluginName(){
        return pluginName;
    }

    public final String getRendererName(){
        return rendererName;
    }

    public final Renderer getRenderer(){
        return renderer;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginName", pluginName)
            .addObject("rendererName", rendererName)
            .addObject("renderer", renderer)
            .toString();
    }

}
