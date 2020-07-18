package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.*;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class ConfigService {

    private final ConfigurationFile config;

    public final ConfigurationFile getConfigFile(){
        return config;
    }

    public final ConfigurationSection getConfig(){
        return config;
    }

    public ConfigService(final File configFile, final String defaultConfigResource) throws IOException{
        final Logger logger = !Vars.Test.testmode ? Main.getLoggerService().getLogger("Config") : Logger.getLogger("Config");
        logger.info("Started configuration initialization");

        // load default
        final ConfigurationFileImpl def;
        try(final InputStream IN = getClass().getResourceAsStream(Objects.requireNonNull( defaultConfigResource))){
            final ConfigurationFileImpl impl = new ConfigurationFileImpl();
            impl.load(IN);
            def = impl;
        }catch(final NullPointerException e){
            logger.severe("Failed to load default configuration file (none specified)");
            throw e;
        }catch(final ClassCastException | YamlException e){
            logger.severe("Failed to load default configuration file (invalid syntax)" + '\n' + Exceptions.getStackTraceAsString(e));
            throw e;
        }catch(final IOException e){
            logger.warning("Failed to close input stream for " + defaultConfigResource + '\n' + Exceptions.getStackTraceAsString(e));
            throw e;
        }

        // load config
        final ConfigurationFileImpl config = new ConfigurationFileImpl(configFile);
        config.setDefault(def);
        try{
            config.load(configFile);
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");
            if(!configFile.exists())
                try{
                    config.save();
                    logger.info("Created default configuration file");
                }catch(final Throwable e){
                    logger.severe("Failed to save default configuration file" + '\n' + Exceptions.getStackTraceAsString(e));
                }
            else
                logger.warning("Failed to create default configuration file (file already exists)");
        }catch(final NullPointerException ignored){
            logger.severe("Failed to load configuration file (none specified)");
        }catch(final ClassCastException | YamlException e){
            logger.severe("Failed to load configuration file (invalid syntax)" + '\n' + Exceptions.getStackTraceAsString(e));
        }

        config.setDefault(def);
        this.config = config;

        if(Vars.Test.testmode)
            Main.getLocaleService().setLocale(Locale.forLanguageTag(config.getString("locale", "en_us")));
        logger.info("Finished configuration service initialization");
    }

}
