package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.*;

public final class ConfigService {

    private static final Logger logger = Logger.getLogger("WebDir / ConfigService");

    private final ConfigurationFile config = new ConfigurationFileImpl();

    private final File configFile;
    private final File defaultConfigFile;

    //

    public final ConfigurationFile getConfig(){
        return config;
    }

    //

    ConfigService(final File configFile, final File defaultConfigFile){
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;

        final String prefix = "[Config]" + ' ';

        logger.info("Started config initialization");

        try{
            final ConfigurationFile def = new ConfigurationFileImpl();
            def.load(defaultConfigFile);
            config.setDefault(def);
        }catch(final FileNotFoundException e){
            logger.severe(
                    "Failed to load default configuration file (not found)" + '\n' + LoggerService.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
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

        read();
        logger.info("Finished config initialization");
    }

    //

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final boolean read(){
        final boolean hasLocale = locale.getLocale() != null;
        final String prefix = hasLocale ? '[' + locale.getString("config") + ']' + ' ' : "[Config]" + ' ';

        logger.info(
            prefix +
            (
                hasLocale ?
                locale.getString("config.read.start") :
                "Loading configuration file"
            )
        );

        try{
            config.load(configFile);
            logger.info(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.finished") :
                    "Finished loading configuration file"
                )
            );
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.notFound") :
                    "Configuration file not found, creating a new configuration file"
                )
            );

            try{ // this will allow preservation of comments
                Files.copy(new FileInputStream(defaultConfigFile), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info(
                    prefix +
                    (
                        hasLocale ?
                        locale.getString("config.read.created") :
                        "New configuration file created"
                    )
                );
            }catch(final IOException e){
                logger.severe(
                        prefix +
                        (
                        hasLocale ?
                        locale.getString("config.read.notCreate") :
                        "Failed to create configuration file, using default configuration"
                    ) +
                        '\n' + LoggerService.getStackTraceAsString(e)
                );
            }
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                    prefix +
                    (
                    hasLocale ?
                    locale.getString("config.read.badSyntax")  :
                    "Failed to read configuration file (invalid syntax), using default configuration"
                ) +
                    '\n' + LoggerService.getStackTraceAsString(e)
            );
        }catch(final IOException e){
            logger.severe(
                    prefix +
                    (
                    hasLocale ?
                    locale.getString("config.read.failed") :
                    "Failed to read configuration file, using default configuration"
                ) +
                    '\n' + LoggerService.getStackTraceAsString(e)
            );
        }
        return false;
    }

    public synchronized final boolean write(){
        final boolean hasLocale = locale.getLocale() != null;
        final String prefix = hasLocale ? '[' + locale.getString("config") + ']' + ' ' : "[Config]" + ' ';

        logger.info(
            (
                hasLocale ?
                locale.getString("config.write.start") :
                "Writing to configuration file"
            )
        );

        try{
            config.save(configFile);
            logger.info(
                (
                    hasLocale ?
                    locale.getString("config.write.finished") :
                    "Finished writing to configuration file"
                )
            );
            return true;
        }catch(final YamlException e){
            logger.severe(
                    prefix +
                    (
                    hasLocale ?
                    locale.getString("config.write.badSyntax")  :
                    "Failed to write to configuration file (invalid syntax)"
                ) +
                    '\n' + LoggerService.getStackTraceAsString(e)
            );
        }catch(IOException e){
           logger.severe(
                   prefix +
                   (
                    hasLocale ?
                    locale.getString("config.write.failed") :
                    "Failed to write to configuration file"
                ) +
                   '\n' + LoggerService.getStackTraceAsString(e)
            );
        }
        return false;
    }

}
