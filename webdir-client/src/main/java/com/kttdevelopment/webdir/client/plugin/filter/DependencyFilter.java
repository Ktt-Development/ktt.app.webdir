package com.kttdevelopment.webdir.client.plugin.filter;

import com.amihaiemil.eoyaml.*;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.YamlUtility;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class DependencyFilter implements Filter<Map<File,YamlMapping>> {


    private final LocaleService locale;
    private final Logger logger;

    public DependencyFilter(){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("plugin-loader.name"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,YamlMapping> filter(final Map<File,YamlMapping> in){
        final Map<File,YamlMapping> deps = new HashMap<>();
        // remove plugins with missing deps
        {
            final List<String> plugins = new ArrayList<>();
            for(final YamlMapping value : in.values())
                plugins.add(value.string(PluginLoader.NAME));

            in.forEach((file, map) -> {
                for(final String dependency : getDependencies(map))
                    if(!plugins.contains(dependency))
                        return; // skip add if missing dep
                deps.put(file, map);
            });
        }

        final Map<File,YamlMapping> safeDeps = new HashMap<>();
        // remove plugins with circular deps
        {
            final List<YamlMapping> plugins = List.copyOf(deps.values());
            deps.forEach((file, yml) -> {
                if(new CircularDependencyChecker(yml, plugins).test()) // if has:
                    logger.severe(locale.getString("plugin-loader.filter.dep.circular", file.getName()));
                else
                    safeDeps.put(file, yml);
            });
        }

        final Map<File,YamlMapping> sortDeps = new LinkedHashMap<>();
        // sort dependency loading order
        {
            final int total = safeDeps.size();
            final List<Map.Entry<File,YamlMapping>> queue = new ArrayList<>(safeDeps.entrySet());
            int index = 0;

            // sort so dependencies load first, dependents last
            while(sortDeps.size() < total){
                final Map.Entry<File,YamlMapping> iterator = queue.get(index);
                final List<YamlMapping> unloadedDependencies = getDependencies(iterator.getValue(), new ArrayList<>(safeDeps.values()));

                // remove dependency if it is already loaded
                unloadedDependencies.removeIf(dependency -> {
                    for(final YamlMapping dep : sortDeps.values())
                        if(dep.string(PluginLoader.NAME).equals(dependency.string(PluginLoader.NAME)))
                            return true;
                    return false;
                });

                // add to sorted if all dependencies have been loaded (or would be loaded) otherwise move to end of queue
                if(unloadedDependencies.isEmpty())
                    sortDeps.put(iterator.getKey(), iterator.getValue());
                else
                    queue.add(iterator);
                index++;
            }
        }
        return sortDeps;
    }

    static List<String> getDependencies(final YamlMapping plugin){
        for(final YamlNode key : plugin.keys()){
            if(YamlUtility.asString(key).equals(PluginLoader.DEPENDENCIES)){
                final String dep = plugin.string(key);
                if(dep != null)
                    return new ArrayList<>(List.of(dep));
                else{
                    final List<String> deps = new ArrayList<>();
                    for(final YamlNode node : plugin.yamlSequence(key)){
                        final String dependencyName = YamlUtility.asString(node);
                        if(dependencyName != null)
                            deps.add(dependencyName);
                    }
                    return deps;
                }
            }
        }
        return new ArrayList<>();
    }

    static List<YamlMapping> getDependencies(final YamlMapping plugin, final List<YamlMapping> plugins){
        final Map<String,YamlMapping> map = new HashMap<>();
        for(final YamlMapping yml : plugins)
            map.put(yml.string(PluginLoader.NAME), yml);
        return getDependencies(plugin, map);
    }

    static List<YamlMapping> getDependencies(final YamlMapping plugin, final Map<String,YamlMapping> plugins){
        final List<YamlMapping> required = new ArrayList<>();
        for(final String dependency : getDependencies(plugin))
            required.add(plugins.get(dependency));
        return required;
    }

}
