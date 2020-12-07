package com.kttdevelopment.webdir.client.plugin.filter;

import com.kttdevelopment.webdir.client.PluginLoader;
import com.kttdevelopment.webdir.client.utility.*;

import java.util.*;

final class CircularDependencyChecker  {

    private final String pluginName;
    private final Map<String,Object> plugin;
    private final List<Map<String,Object>> plugins;

    public CircularDependencyChecker(final Map<String,Object> plugin, final List<Map<String,Object>> plugins){
        this.pluginName = plugin.get(PluginLoader.NAME).toString();
        this.plugin     = plugin;
        this.plugins    = Collections.unmodifiableList(plugins);
    }

    // TRUE if has a circular dependency
    public final boolean test(){
        final List<String> parent = List.of(pluginName);
        for(final Map<String,Object> dependency : DependencyFilter.getDependencies(plugin, plugins))
            if(test(dependency, new ArrayList<>(parent)))
                return true;
        return false;
    }

    private boolean test(final Map<String,Object> plugin, final List<String> checked){
        checked.add(plugin.get(PluginLoader.NAME).toString());
        for(final Map<String,Object> dependency : DependencyFilter.getDependencies(plugin, plugins)){
            final String dependencyName = dependency.get(PluginLoader.NAME).toString();
            if(checked.contains(dependencyName)) // if dependency already requested
                return true;
            else{
                checked.add(dependencyName); // check dependency dependencies
                if(test(dependency, checked))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginName", pluginName)
            .addObject("plugin", plugin)
            .addObject("plugins", plugins)
            .toString();
    }

}
