package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.client.function.Filter;
import com.kttdevelopment.webdir.client.object.Tuple2;

import java.io.File;
import java.util.*;

public final class DependencySorter implements Filter<Map<File,PluginYml>> {

    @Override
    public final Map<File,PluginYml> filter(final Map<File,PluginYml> in){
        final Map<File,PluginYml> sorted = new LinkedHashMap<>();

        // initialize queue
        final int total = in.size();
        final List<Tuple2<File,PluginYml>> queue = new ArrayList<>();
        in.forEach((file,yml) -> queue.add(new Tuple2<>(file, yml)));
        int index = 0;

        // sort so dependencies load first
        while(sorted.size() < total){
            final Tuple2<File,PluginYml> iterator = queue.get(index);
            final List<String> unloadedDependencies = Arrays.asList(iterator.getVar2().getDependencies());
            // remove dependencies already loaded (in the sort list)
            unloadedDependencies.removeIf(dependencyName -> {
                for(final PluginYml dependency : sorted.values())
                    if(dependencyName.equals(dependency.getPluginName()))
                        return true;
                return false;
            });

            // add to sorted if all dependencies have been loaded (or would've been loaded)
            if(unloadedDependencies.isEmpty())
                sorted.put(iterator.getVar1(),iterator.getVar2());
            else
                queue.add(iterator);
            index++;
        }

        return sorted;
    }

}
