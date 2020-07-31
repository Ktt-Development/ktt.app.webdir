package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class ConfigService {

    private final String configFile, defaultConfigResource;

    private final ConfigurationSection config;

    //

    public final ConfigurationSection getConfig(){
        return config;
    }

    //

    public ConfigService(final File configFile, final String defaultConfigResource) throws IOException{
        Objects.requireNonNull(configFile);
        this.configFile = configFile.getAbsolutePath();
        this.defaultConfigResource = defaultConfigResource;
        final Logger logger = Main.getLoggerService() != null ? Main.getLoggerService().getLogger("Config") : Logger.getLogger("Config");

        logger.info("Started configuration initialization");

    // load default
        final ConfigurationFile def;
        logger.finer("Loading default configuration from resource " + defaultConfigResource);
        try(final InputStream IN = getClass().getResourceAsStream(Objects.requireNonNull( defaultConfigResource))){
            final ConfigurationFile impl = new ConfigurationFile();
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
        final ConfigurationFile config = new ConfigurationFile();
        config.setDefault(def);
        try{
            logger.finer("Loading configuration file from " + configFile.getAbsolutePath());
            config.load(configFile);
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");
            if(!configFile.exists())
                try(final InputStream IN = getClass().getResourceAsStream(defaultConfigResource)){
                    Files.copy(IN, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Created default configuration file");
                }catch(final Throwable e){
                    logger.severe("Failed to save default configuration" + '\n' + Exceptions.getStackTraceAsString(e));
                }
            else
                logger.warning("Failed to create default configuration file (file already exists)");
        }catch(final ClassCastException | YamlException e){
            logger.severe("Failed to load configuration file (invalid syntax)" + '\n' + Exceptions.getStackTraceAsString(e));
        }

        config.setDefault(def);
        this.config = config;

        logger.fine("Loaded configuration:\n" + config);

        if(Main.getLoggerService() != null)
            Main.getLocaleService().setLocale(Locale.forLanguageTag(config.getString("locale", "en_us")));
        logger.info("Finished configuration service initialization");
    }

    //


    @Override
    public String toString(){
        return new toStringBuilder("ConfigService")
            .addObject("configurationFile",configFile)
            .addObject("defaultConfigurationResource",defaultConfigResource)
            .addObject("configuration",config)
            .toString();
    }

}
