package com.kttdevelopment.webdir.client.plugins;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.Renderer;

public final class PluginRendererEntry {

    private final String pluginName, rendererName;
    private final Renderer renderer;

    public PluginRendererEntry(final String pluginName, final String rendererName, final Renderer renderer){
        this.pluginName     = pluginName;
        this.rendererName   = rendererName;
        this.renderer       = renderer;
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

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginName",pluginName)
            .addObject("renderName",rendererName)
            .addObject("renderer",renderer)
            .toString();
    }

}
