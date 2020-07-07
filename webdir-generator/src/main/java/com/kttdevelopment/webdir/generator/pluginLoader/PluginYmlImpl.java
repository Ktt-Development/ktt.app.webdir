package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.util.Arrays;
import java.util.Objects;

public class PluginYmlImpl extends PluginYml {

    private final String pluginName, pluginVersion;
    private final String[] authors;
    private final ConfigurationSection config;

    public PluginYmlImpl(final ConfigurationSection config){
        this.pluginName = Objects.requireNonNull(config.getString("name"));
        this.pluginVersion = config.getString("version");
        this.authors = (String[]) config.getList("authors",String.class).toArray();
        this.config = config;
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
    public String[] getAuthors(){
        return Arrays.copyOf(authors,authors.length);
    }

    @Override
    public ConfigurationSection getConfiguration(){
        return null; // todo: create clone
    }

}
