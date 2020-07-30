package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.object.Tuple3;

public final class PluginRendererEntry extends Tuple3<String,String,Renderer> {

    public PluginRendererEntry(final String pluginName, final String rendererName, final Renderer renderer){
        super(pluginName,rendererName,renderer);
    }

    public final String getPluginName(){
        return getVar1();
    }

    public final String getRendererName(){
        return getVar2();
    }

    public final Renderer getRenderer(){
        return getVar3();
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PluginRenderEntry")
            .addObject("pluginName",getPluginName())
            .addObject("rendererName",getRendererName())
            .addObject("renderer",getRenderer())
            .toString();
    }

}
