package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

import static com.kttdevelopment.webdir.Logger.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class Config {

    private static final File configFile = new File(Application.parent + '\\' + "config.yml");
    private static Map config;

    @SuppressWarnings("ConstantConditions") // file must exist
    private static final File defaultConfigFile = new File(Config.class.getClassLoader().getResource("config/config.yml").getFile());
    private static Map defaultConfig;

    public static Object get(final String key){
        return config.getOrDefault(key,defaultConfig.get(key));
    }

    public synchronized static void set(final String key, final Object value){
        config.put(key,value);
        write();
    }

    private static boolean init = false;
    public synchronized static void main(){
        if(init) return; else init = true;

        logger.log(Level.INFO,"[Config] Started config initialization");

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new FileReader(defaultConfigFile));
            defaultConfig = (Map) IN.read();
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe(
                "[Config] Failed to load default configuration file" +
                (e instanceof YamlException ? ' ' + "(invalid syntax)" + '\n' + getStackTraceAsString(e) : "")
            );
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.severe("[Config] Failed to close default config input stream" + '\n' + getStackTraceAsString(e));
                }
        }

        read();
        logger.info("[Config] Finished config initialization");
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized static boolean read(){
        final boolean hasLocale = Locale.getLocale() != null;
        logger.info(
            hasLocale ?
            '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.initial") :
            "[Config] Loading config from file"
        );

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.info(
                hasLocale ?
                '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.finished") :
                "[Config] Finished loading config from file"
            );
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(
                hasLocale ?
                '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.notFound") :
                "[Config] Config file not found, creating a new config file"
            );
            config = defaultConfig;
            if(!write())
                logger.severe(
                    hasLocale ?
                    '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.notCreate") :
                    "[Config] Failed to create config file, using default config"
                );
            else
                logger.info(
                    hasLocale ?
                    '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.created") :
                    "[Config] New config file created"
                );
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                (
                    hasLocale ?
                    '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.badSyntax")  :
                    "[Config] Failed to read config file (invalid syntax)"
                ) +
                '\n' + getStackTraceAsString(e)
            );
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(
                        (
                            hasLocale ?
                            '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.read.stream") : "[Config] Failed to close config input stream"
                        ) +
                        '\n' + getStackTraceAsString(e)
                    );
                }
        }
        return false;
    }

    public synchronized static boolean write(){
        final boolean hasLocale = Locale.getLocale() != null;
        logger.info(
            hasLocale ?
            '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.write.initial") :
            "[Config] Writing config to file"
        );

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            logger.info(
                hasLocale ?
                '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.write.finished") :
                "[Config] Finished writing config to file"
            );
            return true;
        }catch(final IOException e){
            logger.severe(
                (
                    hasLocale ?
                    '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.write.failed") :
                    "[Config] Failed to write config to file"
                ) +
                '\n' + getStackTraceAsString(e)
            );
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe(
                        (
                            hasLocale ?
                            '[' + Locale.getString("config") + ']' + ' ' + Locale.getString("config.write.stream") :
                            "[Config] Failed to close config output stream"
                        ) +
                        '\n' + getStackTraceAsString(e)
                    );
                }
        }
        return false;
    }

}
