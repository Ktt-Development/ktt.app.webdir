package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.plugins.JarFilter;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public final class PluginLoader {

    private final File pluginFolder;

    PluginLoader(final File pluginFolder){
        final LocaleService locale          = Main.getLocaleService();
        final ConfigurationSection config   = Main.getConfigService().getConfig();
        final Logger logger                 = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));

        logger.info(locale.getString("pluginLoader.const.started"));

        this.pluginFolder = pluginFolder;

        if(config.getBoolean("safe")){
            logger.info(locale.getString("pluginLoader.const.safe"));
            return;
        }

        logger.fine(locale.getString("pluginLoader.const.filter.jar"));
        // jar filter
        final Map<File,URL> pluginJars = new LinkedHashMap<>();
        {
            final File[] plugins = Objects.requireNonNullElse(pluginFolder.listFiles(new JarFilter()), new File[0]);
            Arrays.sort(plugins);
            for(final File file : plugins){
                
            }
        }

    }

}
