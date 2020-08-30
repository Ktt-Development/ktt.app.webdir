package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.plugins.filter.*;

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

        // jar filter
        logger.fine(locale.getString("pluginLoader.const.filter.jar"));
        final Map<File,URL> pluginJars = new JarFilter().filter(pluginFolder);

        // plugin.yml filter
        logger.fine(locale.getString("pluginLoader.const.filter.yml"));
        final Map<File,URL> pluginYMLs = new PluginYMLFilter().filter(pluginJars);

        // validate plugin.yml
        logger.fine(locale.getString("pluginLoader.const.filter.validYML"));
        final Map<File,PluginYml> validYMLs = new ValidPluginYMLFilter().filter(pluginYMLs);

        // validate dep
        logger.finest(locale.getString("pluginLoader.const.filter.validDep"));

        // sort dep

        // enable & check #main

    }

}
