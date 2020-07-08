package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.generator.object.BiTuple;

public final class PluginRenderer extends BiTuple<String,String> {

    public PluginRenderer(final String pluginName, final String rendererName){
        super(pluginName,rendererName);
    }

    public final String getPluginName(){
        return getVar1();
    }

    public final String getRendererName(){
        return getVar2();
    }

    @Override
    public String toString(){
        return
            "Tuple" + '{' +
            "pluginName"    + '=' + getVar1() + ", " +
            "rendererName"  + '=' + getVar2() + ", " +
            '}';
    }

}
