package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.PluginYml;

import java.util.*;

// true if has circular dependency
final class CircularDependencyChecker {

    private final PluginYml plugin;
    private final Map<String,PluginYml> plugins = new LinkedHashMap<>();

    private final String pluginName;

    public CircularDependencyChecker(final PluginYml plugin, final List<PluginYml> plugins){
        this.plugin = plugin;

        for(final PluginYml pluginYml : plugins)
            this.plugins.put(pluginYml.getPluginName(),pluginYml);

        pluginName = plugin.getPluginName();
    }

    // test each dependency for a circular loop
    public final boolean test(){
        final List<String> parent = List.of(pluginName);
        for(final PluginYml dependency : getDependencies(plugin))
            if(test(dependency,new ArrayList<>(parent)))
                return true;
        return false;
    }

    // test each dependency for a circular reference
    private boolean test(final PluginYml plugin, final List<String> checked){
        checked.add(plugin.getPluginName());
        for(final PluginYml dependency : getDependencies(plugin)){
            final String dependencyName = dependency.getPluginName();
            if(checked.contains(dependencyName))
                return true;
            else{
                checked.add(dependencyName);
                if(test(dependency,checked))
                    return true;
            }
        }
        return false;
    }

    private List<PluginYml> getDependencies(final PluginYml plugin){
        final List<String> required = Arrays.asList(plugin.getDependencies());
        final List<PluginYml> dependencies = new ArrayList<>();

        plugins.forEach((name,yml) -> {
            if(required.contains(name))
                dependencies.add(yml);
        });
        return dependencies;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("plugin",plugin)
            .addObject("plugins",plugins)
            .addObject("pluginName",pluginName)
            .toString();
    }

}
