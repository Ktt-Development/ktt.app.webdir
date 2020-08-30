package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.client.function.Filter;

import java.io.File;
import java.util.*;

public final class DependencyFilter implements Filter<Map<File, PluginYml>> {

    @Override
    public final Map<File,PluginYml> filter(final Map<File,PluginYml> in){
        final Map<File,PluginYml> map = new LinkedHashMap<>();
        final List<String> plugins = new ArrayList<>();
        // remove circular dependencies
        in.forEach((file,yml) -> {
            if(false /* no circular */)
                plugins.add(yml.getPluginName());
            else; // severe
        });
        // remove missing
        in.forEach((file,yml) -> {
            for(final String dependency : yml.getDependencies()){
                if(!plugins.contains(dependency));
                else
                    map.put(file,yml);
            }
        });

        return map;
    }

}
