package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.PluginYml;

import java.util.*;

// true if has circular dependency
final class CircularDependencyChecker {

    private final PluginYml plugin;
    private final Map<String,PluginYml> plugins;

    public CircularDependencyChecker(final PluginYml plugin, final Map<String,PluginYml> plugins){
        this.plugin = plugin;
        this.plugins = plugins;
    }

    private final List<String> checked = new ArrayList<>();

    public final boolean test(){
        return test(plugin);
    }

    private boolean test(final PluginYml plugin){
        return false;
    }

    private List<PluginYml> getDependencies(final PluginYml plugin){
        final List<String> deps = Arrays.asList(plugin.getDependencies());
        final List<PluginYml> dependencies = new ArrayList<>();

        plugins.forEach((name,yml) -> {

        });
        return null;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("plugin",plugin)
            .addObject("plugins",plugins)
            .addObject("checked",checked)
            .toString();
    }

}
