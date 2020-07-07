package com.kttdevelopment.webdir.generator;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class ConfigService {

    private final ConfigurationFile config;

    public final ConfigurationSection getConfig(){
        return config;
    }

    private final File configFile;
    private final String defaultConfigResource;

    ConfigService(final File configFile, final String defaultConfigResource) throws IOException{
        this.configFile = configFile;
        this.defaultConfigResource = defaultConfigResource;

        Logger logger = Logger.getLogger("Config");
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
        ConfigurationFile config = new ConfigurationFileImpl(configFile);
        try{
            final ConfigurationFileImpl impl = new ConfigurationFileImpl();
            impl.load(configFile);
            config = impl;
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");
            copyDefaultConfig();
        }catch(final NullPointerException e){
            logger.severe("Failed to load configuration file (none specified)");
        }catch(final ClassCastException | YamlException e){
            logger.severe("Failed to load configuration file (invalid syntax)" + '\n' + Exceptions.getStackTraceAsString(e));
        }

        config.setDefault(def);
        this.config = config;

        final LocaleService locale = Main.getLocaleService();
        locale.setLocale(Locale.forLanguageTag(config.getString("locale", "en_us")));
        logger = Logger.getLogger(locale.getString("config"));
        logger.info(locale.getString("config.const.loaded"));
    }

    private synchronized void copyDefaultConfig(){
        final Logger logger = Logger.getLogger("Config");
        logger.fine("Creating default configuration file");

        if(!configFile.exists()){
            try(final InputStream IN = getClass().getClassLoader().getResourceAsStream(defaultConfigResource)){
                Files.copy(Objects.requireNonNull(IN), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Created default configuration file");
            }catch(final NullPointerException ignored){
                logger.severe("Failed to load default configuration file (not found)");
            }catch(final IOException e){
                logger.warning("Failed to close default configuration input stream (I/O exception)" + '\n' + Exceptions.getStackTraceAsString(e));
            }
        }else{
            logger.severe("Failed to create default configuration file (file already exists)");
        }
    }

}
