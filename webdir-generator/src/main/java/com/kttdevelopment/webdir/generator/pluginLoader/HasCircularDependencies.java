package com.kttdevelopment.webdir.generator.pluginLoader;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.object.Tuple4;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class HasCircularDependencies implements Predicate<Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml>> {

    private final String pluginName;

    private final List<Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml>> plugins;

    public HasCircularDependencies(final Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml> plugin, final List<Tuple4<File, Class<WebDirPlugin>, ConfigurationSection, PluginYml>> plugins){
        pluginName = plugin.getVar4().getPluginName();
        this.plugins = plugins;
    }

    @Override
    public final boolean test(final Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml> plugin){
        final List<Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml>> dependencies = getDependencies(plugin);
        // for each dependency, check if it matches the plugin name, or if its' dependencies match the plugin name
        for(final Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml> dependency : dependencies)
            if(dependency.getVar4().getPluginName().equals(pluginName) || test(dependency))
                return true;
        return false;
    }

    private List<Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml>> getDependencies(final Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml> plugin){
        final List<String> strDeps = Arrays.asList(plugin.getVar4().getDependencies());
        final List<Tuple4<File,Class<WebDirPlugin>,ConfigurationSection,PluginYml>> dependencies = new ArrayList<>();

        for(final Tuple4<File, Class<WebDirPlugin>, ConfigurationSection, PluginYml> tuple : plugins)
            if(strDeps.contains(tuple.getVar4().getPluginName()))
                dependencies.add(tuple);
        return dependencies;
    }

}
