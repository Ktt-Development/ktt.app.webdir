package com.kttdevelopment.webdir.client.plugin.filter;

import com.kttdevelopment.webdir.client.PluginLoader;
import com.kttdevelopment.webdir.client.utility.*;

import java.util.*;

final class CircularDependencyChecker  {

    private final String pluginName;
    private final YamlMapping plugin;
    private final List<YamlMapping> plugins;

    public CircularDependencyChecker(final YamlMapping plugin, final List<YamlMapping> plugins){
        this.pluginName = plugin.string(PluginLoader.NAME);
        this.plugin     = plugin;
        this.plugins    = Collections.unmodifiableList(plugins);
    }

    // TRUE if has a circular dependency
    public final boolean test(){
        final List<String> parent = List.of(pluginName);
        for(final YamlMapping dependency : DependencyFilter.getDependencies(plugin, plugins))
            if(test(dependency, new ArrayList<>(parent)))
                return true;
        return false;
    }

    private boolean test(final YamlMapping plugin, final List<String> checked){
        checked.add(plugin.string(PluginLoader.NAME));
        for(final YamlMapping dependency : DependencyFilter.getDependencies(plugin, plugins)){
            final String dependencyName = dependency.string(PluginLoader.NAME);
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
