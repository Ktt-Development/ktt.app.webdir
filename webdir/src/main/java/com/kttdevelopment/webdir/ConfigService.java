package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class ConfigService {

    private final File configFile;
    private Map config;

    private final Map defaultConfig;

    //

    public final Object get(final String key){
        return config.getOrDefault(key,defaultConfig.get(key));
    }

    public synchronized final void set(final String key, final Object value){
        config.put(key,value);
        write();
    }

    //

    ConfigService(final File configFile, final File defaultConfigFile){
        this.configFile = configFile;

        final String prefix = "[Config]" + ' ';

        logger.info(prefix + "Started config initialization");

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new FileReader(defaultConfigFile));
            defaultConfig = (Map) IN.read();
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe(
                prefix + "Failed to load default config file" +
                (e instanceof YamlException ? ' ' + "(invalid syntax)" + '\n' + Logger.getStackTraceAsString(e) : "")
            );
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(prefix + "Failed to close default config input stream" + '\n' + Logger.getStackTraceAsString(e));
                }
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
                "Loading config from file"
            )
        );

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.info(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.finished") :
                    "Finished loading config from file"
                )
            );
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.notFound") :
                    "Config file not found, creating a new config file"
                )
            );
            config = defaultConfig;
            if(!write())
                logger.severe(
                    prefix +
                    (
                        hasLocale ?
                        locale.getString("config.read.notCreate") :
                        "Failed to create config file, using default config"
                    )
                );
            else
                logger.info(
                    prefix +
                    (
                        hasLocale ?
                        locale.getString("config.read.created") :
                        "New config file created"
                    )
                );
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.read.badSyntax")  :
                    "Failed to read config file (invalid syntax)"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(
                        prefix +
                        (
                            hasLocale ?
                            locale.getString("config.read.stream") :
                            "Failed to close config input stream"
                        ) +
                        '\n' + Logger.getStackTraceAsString(e)
                    );
                }
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
                "Writing config to file"
            )
        );

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            logger.info(
                prefix + (
                    hasLocale ?
                    locale.getString("config.write.finished") :
                    "Finished writing config to file"
                )
            );
            return true;
        }catch(final IOException e){
            logger.severe(
                prefix +
                (
                    hasLocale ?
                    locale.getString("config.write.failed") :
                    "Failed to write config to file"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe(
                        prefix +
                        (
                            hasLocale ?
                            locale.getString("config.write.stream") :
                            "Failed to close config output stream"
                        ) +
                        '\n' + Logger.getStackTraceAsString(e)
                    );
                }
        }
        return false;
    }

}
