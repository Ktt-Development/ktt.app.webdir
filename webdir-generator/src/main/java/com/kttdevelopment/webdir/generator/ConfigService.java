package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class ConfigService {

    private final ConfigurationSection config;

    public final ConfigurationSection getConfig(){
        return config;
    }

    public ConfigService(final File configFile, final String defaultConfigResource) throws IOException{
        final Logger logger = !Vars.Test.testmode ? Main.getLoggerService().getLogger("Config") : Logger.getLogger("Config");
        logger.info("Started configuration initialization");

        // load default
        final ConfigurationFile def;
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
            config.load(configFile);
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");
            if(!configFile.exists())
                try(final InputStream IN = getClass().getResourceAsStream(defaultConfigResource)){
                    Files.copy(Objects.requireNonNull(IN), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Created default configuration file");
                }catch(final NullPointerException e){
                    logger.severe("Failed to save default configuration file (not found)" + '\n' + Exceptions.getStackTraceAsString(e));
                }catch(final IOException e){
                    logger.severe("Failed to save default configuration file (I/O exception)" + '\n' + Exceptions.getStackTraceAsString(e));
                }catch(final Throwable e){
                    logger.severe("Failed to save default configuration" + '\n' + Exceptions.getStackTraceAsString(e));
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

        if(!Vars.Test.testmode)
            Main.getLocaleService().setLocale(Locale.forLanguageTag(config.getString("locale", "en_us")));
        logger.info("Finished configuration service initialization");
    }

}
