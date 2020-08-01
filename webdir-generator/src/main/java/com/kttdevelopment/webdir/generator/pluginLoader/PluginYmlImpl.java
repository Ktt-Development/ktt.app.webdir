package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.util.*;

public final class PluginYmlImpl extends PluginYml {

    private final String pluginName, pluginVersion;
    private final String[] authors;
    private final String[] dependencies;
    private final ConfigurationSection config;

    public PluginYmlImpl(final ConfigurationSection config){
        Objects.requireNonNull(config);
        this.pluginName     = Objects.requireNonNull(config.getString(Vars.Plugin.nameKey));
        this.pluginVersion  = config.getString(Vars.Plugin.versionKey);
        this.authors        = config.getList(Vars.Plugin.authorsKey, new ArrayList<String>()).toArray(new String[0]);
        this.dependencies   = config.getList(Vars.Plugin.dependenciesKey,new ArrayList<String>()).toArray(new String[0]);
        this.config         = config;
    }

    @Override
    public final String getPluginName(){
        return pluginName;
    }

    @Override
    public final String getPluginVersion(){
        return pluginVersion;
    }

    @Override
    public final String getAuthor(){
        return authors[0];
    }

    @Override
    public final String[] getAuthors(){
        return Arrays.copyOf(authors,authors.length);
    }

    @Override
    public String[] getDependencies(){
        return Arrays.copyOf(dependencies,dependencies.length);
    }

    @Override
    public final ConfigurationSection getConfiguration(){
        return new ConfigurationSectionImpl(config.toMap());
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof PluginYml))
            return false;
        final PluginYml other = ((PluginYml) o);
        return other.getPluginName().equals(pluginName) &&
               other.getPluginVersion().equals(pluginVersion) &&
               Arrays.equals(other.getAuthors(),authors) &&
               Arrays.equals(other.getDependencies(),dependencies) &&
               other.getConfiguration().equals(config);
    }

    @Override
    public String toString(){
        return new toStringBuilder("PluginYml")
            .addObject("pluginName",pluginName)
            .addObject("pluginVersion",pluginVersion)
            .addObject("authors",authors)
            .addObject("dependencies",dependencies)
            .addObject("configuration",config)
            .toString();
    }

}
