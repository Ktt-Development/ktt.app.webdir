package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;

import java.util.*;

public final class PluginYmlImpl extends PluginYml {

    private final String pluginName, pluginVersion;
    private final String[] authors;
    private final String[] dependencies;
    private final ConfigurationSection config;

    public PluginYmlImpl(final ConfigurationSection config){
        Objects.requireNonNull(config);
        this.pluginName     = Objects.requireNonNull(config.getString("name"));
        this.pluginVersion  = config.getString("version");
        this.authors        = config.getList("authors", new ArrayList<String>()).toArray(new String[0]);
        this.dependencies   = config.getList("dependencies",new ArrayList<String>()).toArray(new String[0]);
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

}
