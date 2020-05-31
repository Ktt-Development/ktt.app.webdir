package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
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

    ConfigService(final File configFile, final InputStream defaultConfig){
        logger.info("Started config initialization");

        final InputStream configStream, cloneStream;

        try(final ByteArrayOutputStream OUT = new ByteArrayOutputStream()){
            Objects.requireNonNull(defaultConfig).transferTo(OUT);
            configStream = new ByteArrayInputStream(OUT.toByteArray());
            cloneStream = new ByteArrayInputStream(OUT.toByteArray());
        }catch(final NullPointerException e){
            logger.severe(
                "Failed to load default configuration file (not found)"
            );
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe("Failed to read default configuration file (I/O error)" + '\n' + LoggerService.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }

        // load default

        final ConfigurationFile def;
        try(defaultConfig){
            def = new ConfigurationFileImpl(configStream);
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                "Failed to load default configuration file (invalid syntax)" + '\n' + LoggerService.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe(
                "Failed to load default configuration file" + '\n' + LoggerService.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
        }

        // load config
        ConfigurationFile tConfig = new ConfigurationFileImpl(configFile,true);
        tConfig.setDefault(def);
        try{
            tConfig = new ConfigurationFileImpl(configFile);
            logger.info("Finished loading configuration file");
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");

            try(cloneStream){ // this will allow preservation of comments
                Files.copy(cloneStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("New configuration file created");
            }catch(final IOException e){
                logger.severe(
                    "Failed to create configuration file, using default configuration" +
                    '\n' + LoggerService.getStackTraceAsString(e)
                );
            }
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                "Failed to read configuration file (invalid syntax), using default configuration" +
                '\n' + LoggerService.getStackTraceAsString(e)
            );
        }
        config = tConfig;

        logger.info("Finished config initialization");
    }


}
