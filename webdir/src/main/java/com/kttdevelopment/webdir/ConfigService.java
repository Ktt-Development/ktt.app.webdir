package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;

import java.io.File;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.*;

public final class ConfigService {

    private static final Logger logger = Logger.getLogger("WebDir / ConfigService");

    private final ConfigurationFile config;

    //

    public final ConfigurationFile getConfig(){
        return config;
    }

    //

    ConfigService(final File configFile, final File defaultConfigFile){
        logger.info("Started config initialization");

        final ConfigurationFile def;
        def = new ConfigurationFileImpl(defaultConfigFile);

        config = new ConfigurationFileImpl(configFile); // use file exists for err
        config.setDefault(def);
        config.saveDefault();

        logger.info("Loading configuration file");
        config.reload();
        logger.info("Finished loading configuration file");


        logger.info("Finished config initialization");
    }

    //

    public synchronized final void read(){ // replace with reload
        final boolean hasLocale = locale.getLocale() != null;
        logger.info(
            hasLocale ?
            locale.getString("config.read.start") :
            "Loading configuration file"
        );
        config.reload();
        logger.info(
            hasLocale ?
            locale.getString("config.read.finished") :
            "Finished loading configuration file"
        );
    }

    public synchronized final void save(){
        config.save();
    }
}
