package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;

import java.io.*;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

public final class ConfigService {

    private final ConfigurationFile config = new ConfigurationFileImpl();

    private final File configFile;

    //

    public final ConfigurationFile getConfig(){
        return config;
    }

    //

    ConfigService(final File configFile, final File defaultConfigFile){
        this.configFile = configFile;

        final String prefix = "[Config]" + ' ';

        logger.info(prefix + "Started config initialization");

        try{
            final ConfigurationFile def = new ConfigurationFileImpl();
            def.load(defaultConfigFile);
            config.setDefault(def);
        }catch(final FileNotFoundException e){
            logger.severe(
                prefix + "Failed to load default configuration file (not found)" + '\n' + Logger.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                prefix + "Failed to load default configuration file (invalid syntax)" + '\n' + Logger.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe(
                prefix + "Failed to load default configuration file" + '\n' + Logger.getStackTraceAsString(e)
            );
            throw new RuntimeException(e);
        }

        read();
        logger.info(prefix + "Finished config initialization");
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

            if(write())
                logger.info(
                    prefix +
                    (
                        hasLocale ?
                        locale.getString("config.read.created") :
                        "New configuration file created"
                    )
                );
            else
                logger.severe(
                    prefix +
                    (
                        hasLocale ?
                        locale.getString("config.read.notCreate") :
                        "Failed to create configuration file, using default configuration"
                    )
                );
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.badSyntax")  :
                    "Failed to read configuration file (invalid syntax), using default configuration"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }catch(final IOException e){
            logger.severe(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.failed") :
                    "Failed to read configuration file, using default configuration"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }
        return false;
    }

    public synchronized final boolean write(){
        final boolean hasLocale = locale.getLocale() != null;
        final String prefix = hasLocale ? '[' + locale.getString("config") + ']' + ' ' : "[Config]" + ' ';

        logger.info(
            prefix + (
                hasLocale ?
                locale.getString("config.write.start") :
                "Writing to configuration file"
            )
        );

        try{
            config.save(configFile);
            logger.info(
                prefix + (
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
                '\n' + Logger.getStackTraceAsString(e)
            );
        }catch(IOException e){
           logger.severe(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.write.failed") :
                    "Failed to write to configuration file"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }
        return false;
    }

}
