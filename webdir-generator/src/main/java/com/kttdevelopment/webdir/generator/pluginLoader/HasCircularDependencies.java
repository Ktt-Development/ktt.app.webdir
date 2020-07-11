package com.kttdevelopment.webdir.generator.pluginLoader;

import java.util.*;
import java.util.function.Predicate;

public final class HasCircularDependencies implements Predicate<PluginLoaderEntry> {

    private final String pluginName;

    private final List<PluginLoaderEntry> plugins;

    public HasCircularDependencies(final PluginLoaderEntry plugin, final List<PluginLoaderEntry> plugins){
        pluginName = plugin.getPluginYml().getPluginName();
        this.plugins = plugins;
    }

    @Override
    public final boolean test(final PluginLoaderEntry plugin){
        final List<PluginLoaderEntry> dependencies = getDependencies(plugin);
        // for each dependency, check if it matches the plugin name, or if its' dependencies match the plugin name
        for(final PluginLoaderEntry dependency : dependencies)
            if(dependency.getPluginYml().getPluginName().equals(pluginName) || test(dependency))
                return true;
        return false;
    }

    private List<PluginLoaderEntry> getDependencies(final PluginLoaderEntry plugin){
        final List<String> strDeps = Arrays.asList(plugin.getPluginYml().getDependencies());
        final List<PluginLoaderEntry> dependencies = new ArrayList<>();

        for(final PluginLoaderEntry tuple : plugins)
            if(strDeps.contains(tuple.getVar4().getPluginName()))
                dependencies.add(tuple);
        return dependencies;
    }

}
