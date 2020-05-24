package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class Config {

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

    Config(final File configFile, final File defaultConfigFile){
        this.configFile = configFile;

        logger.log(Level.INFO,"[Config] Started config initialization");

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new FileReader(defaultConfigFile));
            defaultConfig = (Map) IN.read();
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe(
                "[Config] Failed to load default configuration file" +
                (e instanceof YamlException ? ' ' + "(invalid syntax)" + '\n' + Logger.getStackTraceAsString(e) : "")
            );
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.severe("[Config] Failed to close default config input stream" + '\n' + Logger.getStackTraceAsString(e));
                }
        }

        read();
        logger.info("[Config] Finished config initialization");
    }

    //

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final boolean read(){
        final boolean hasLocale = locale.getLocale() != null;
        logger.info(
            hasLocale ?
            '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.initial") :
            "[Config] Loading config from file"
        );

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.info(
                hasLocale ?
                '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.finished") :
                "[Config] Finished loading config from file"
            );
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(
                hasLocale ?
                '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.notFound") :
                "[Config] Config file not found, creating a new config file"
            );
            config = defaultConfig;
            if(!write())
                logger.severe(
                    hasLocale ?
                    '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.notCreate") :
                    "[Config] Failed to create config file, using default config"
                );
            else
                logger.info(
                    hasLocale ?
                    '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.created") :
                    "[Config] New config file created"
                );
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                (
                    hasLocale ?
                    '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.badSyntax")  :
                    "[Config] Failed to read config file (invalid syntax)"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(
                        (
                            hasLocale ?
                            '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.read.stream") : "[Config] Failed to close config input stream"
                        ) +
                        '\n' + Logger.getStackTraceAsString(e)
                    );
                }
        }
        return false;
    }

    public synchronized final boolean write(){
        final boolean hasLocale = locale.getLocale() != null;
        logger.info(
            hasLocale ?
            '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.write.initial") :
            "[Config] Writing config to file"
        );

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            logger.info(
                hasLocale ?
                '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.write.finished") :
                "[Config] Finished writing config to file"
            );
            return true;
        }catch(final IOException e){
            logger.severe(
                (
                    hasLocale ?
                    '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.write.failed") :
                    "[Config] Failed to write config to file"
                ) +
                '\n' + Logger.getStackTraceAsString(e)
            );
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe(
                        (
                            hasLocale ?
                            '[' + locale.getString("config") + ']' + ' ' + locale.getString("config.write.stream") :
                            "[Config] Failed to close config output stream"
                        ) +
                        '\n' + Logger.getStackTraceAsString(e)
                    );
                }
        }
        return false;
    }

}
