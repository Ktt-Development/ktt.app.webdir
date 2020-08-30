package com.kttdevelopment.webdir.client.plugins.filter;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.function.Filter;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class DependencyFilter implements Filter<Map<File, PluginYml>> {

    private final LocaleService locale;
    private final Logger logger;

    public DependencyFilter(){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public final Map<File,PluginYml> filter(final Map<File,PluginYml> in){
        // remove missing
        final Map<File,PluginYml> validDeps = new LinkedHashMap<>();
        {
            final List<String> plugins = new ArrayList<>();
            for(final PluginYml value : in.values())
                plugins.add(value.getPluginName());

            in.forEach((file, yml) -> {
                final String name = yml.getPluginName();
                for(final String dependency : yml.getDependencies()){
                    if(!plugins.contains(dependency))
                        logger.severe(locale.getString("pluginLoader.validDepFilter.missingRequired", name, dependency));
                    else
                        validDeps.put(file, yml);
                }
            });
        }

        // remove circular
        final Map<File,PluginYml> safeDeps = new LinkedHashMap<>();
        {
            final List<PluginYml> plugins = new ArrayList<>(validDeps.values());

            in.forEach((file,yml) -> {
                if(!(new CircularDependencyChecker(yml,plugins).test()))
                    safeDeps.put(file,yml);
                else
                    logger.severe(locale.getString("pluginLoader.validDepFilter.circularDependency",yml.getPluginName()));
            });
        }
        return safeDeps;
    }

}
