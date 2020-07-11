package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.object.Tuple4;

import java.io.File;

public final class PluginLoaderEntry extends Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml> {

    public PluginLoaderEntry(final File pluginFile, final Class<WebDirPlugin> mainClass, final ConfigurationSection yml, final PluginYml pluginYml){
        super(pluginFile, mainClass, yml, pluginYml);
    }

    public final File getPluginFile(){
        return getVar1();
    }

    public final Class<WebDirPlugin> getMainClass(){
        return getVar2();
    }

    public final ConfigurationSection getYml(){
        return getVar3();
    }

    public final PluginYml getPluginYml(){
        return getVar4();
    }

    @Override
    public String toString(){
        return
            "Tuple3" + '{' +
            "pluginFile"  + '=' + getVar1() + ", " +
            "mainClass"  + '=' + getVar2() + ", " +
            "yml"  + '=' + getVar3() + ", " +
            "pluginYml"  + '=' + getVar4() +
            '}';
    }

}
