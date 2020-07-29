package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.util.*;
import java.util.function.Predicate;

// true if has
public class CircularDependencyChecker implements Predicate<PluginLoaderEntry> {

    private final List<PluginLoaderEntry> plugins;

    public CircularDependencyChecker(final PluginLoaderEntry plugin, final List<PluginLoaderEntry> plugins){
        this.plugins = Collections.unmodifiableList(plugins);
        checked.add(plugin.getPluginYml().getPluginName());
    }

    private final List<String> checked = new ArrayList<>();

    @Override
    public final boolean test(final PluginLoaderEntry plugin){
        final List<PluginLoaderEntry> dependencies = getDependencies(plugin);

        for(final PluginLoaderEntry dependency : dependencies){
            final String dependencyName = dependency.getPluginYml().getPluginName();
            if(checked.contains(dependencyName)){
                return true;
            }else{
                checked.add(dependencyName);
                if(test(dependency))
                    return true;
            }
        }
        return false;
    }

    private List<PluginLoaderEntry> getDependencies(final PluginLoaderEntry plugin){
        final List<String> deps = Arrays.asList(plugin.getPluginYml().getDependencies());
        final List<PluginLoaderEntry> dependencies = new ArrayList<>();

        for(final PluginLoaderEntry entry : plugins)
            if(deps.contains(entry.getPluginYml().getPluginName()))
                dependencies.add(entry);
        return dependencies;
    }

    //


    @Override
    public String toString(){
        return new toStringBuilder("CircularDependencyChecker")
            .addObject("plugins",plugins)
            .addObject("checked",checked)
            .toString();
    }

}
