package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.object.Tuple4;

import java.io.File;

public final class PluginLoaderEntry extends Tuple4<File,String,ConfigurationSection,PluginYml> {

    public PluginLoaderEntry(final File pluginFile, final String mainClassName, final ConfigurationSection yml, final PluginYml pluginYml){
        super(pluginFile, mainClassName,yml, pluginYml);
    }

    public final File getPluginFile(){
        return getVar1();
    }

    public final String getMainClassName(){
        return getVar2();
    }

    public final ConfigurationSection getYml(){
        return getVar3();
    }

    public final PluginYml getPluginYml(){
        return getVar4();
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PluginLoaderEntry")
            .addObject("pluginFile",getPluginFile())
            .addObject("mainClassName",getMainClassName())
            .addObject("yml",getPluginYml())
            .addObject("pluginYml",getPluginYml())
            .toString();
    }

}
