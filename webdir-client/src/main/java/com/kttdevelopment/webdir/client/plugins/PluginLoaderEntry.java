package com.kttdevelopment.webdir.client.plugins;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public final class PluginLoaderEntry {
    
    private final File pluginFile;
    private final String mainClassName;
    private final ConfigurationSection yml;
    private final PluginYml pluginYml;

    public PluginLoaderEntry(final File pluginFile, final String mainClassName, final ConfigurationSection yml, final PluginYml pluginYml){
        this.pluginFile     = pluginFile;
        this.mainClassName  = mainClassName;
        this.yml            = yml;
        this.pluginYml      = pluginYml;
    }

    public final File getPluginFile(){
        return pluginFile;
    }

    public final String getMainClassName(){
        return mainClassName;
    }

    public final ConfigurationSection getYml(){
        return yml;
    }

    public final PluginYml getPluginYml(){
        return pluginYml;
    }

    //


    @Override
    public String toString(){
        return new ToStringBuilder("PluginLoaderEntry")
            .addObject("pluginFile",getPluginFile())
            .addObject("mainClassName",getMainClassName())
            .addObject("yml",getPluginYml())
            .addObject("pluginYml",getPluginYml())
            .toString();
    }

}
