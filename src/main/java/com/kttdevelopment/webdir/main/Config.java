package com.kttdevelopment.webdir.main;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;
import java.util.Objects;

import static com.kttdevelopment.webdir.main.Logger.*;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class Config {

    private static String resourceFolder = "/config";

    private static File configFile = new File(com.kttdevelopment.webdir.main.Main.root + "config.yml");
    private static Map config;

    private static File defaultConfigFile = new File(Config.class.getClassLoader().getResource(resourceFolder.substring(1) + '/' + "config.yml").getFile());
    private static Map defaultConfig;

    public static Object get(final String key){
        final Object value = config.get(key);
        if(Objects.isNull(value))
            logger.severe(String.format(Locale.getString("config.keyNotFound"),key));
        return value;
    }

    public static Object getOrDefault(final String key){
        final Object value = config.get(key);
        if(Objects.isNull(value)){
            final Object def = defaultConfig.get(key);
            config.put(key,def);
            logger.warning(String.format(Locale.getString("config.keyNotFoundUseDefault"),key,def.toString()));
            return def;
        }else return value;
    }

    public static void set(final String key, final Object value){
        config.put(key,value);
    }

    abstract static class Main {

        synchronized static void init(){
            logger.fine("Started config init.");
            try{
                final YamlReader IN = new YamlReader(new FileReader(defaultConfigFile));
                defaultConfig = (Map) IN.read();
            }catch(final FileNotFoundException | YamlException e){ // should never occur in production
                logger.severe(
                    "Failed to load default configuration file" +
                    (e instanceof YamlException ? "(Invalid syntax)." + '\n' + Logger.getStackTraceAsString(e) : '.')
                );
                throw new RuntimeException(e);
            }
            read();
            logger.fine("Finished config init.");
        }
    }

    public synchronized static boolean read(){
        logger.finer("Started config read.");
        try{
            final YamlReader IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.finer("Finished config read.");
            return true;
        }catch(final FileNotFoundException e){
            logger.warning("Configuration file not found, creating new configuration file.");
            config = defaultConfig;
            if(!write())
                logger.severe("Failed to create configuration file, using default configuration file.");
            else
                logger.info("New configuration file created.");
        }catch(final YamlException e){
            logger.warning("Configuration file syntax is incorrect, using default configuration file." + '\n' + Logger.getStackTraceAsString(e));
            config = defaultConfig;
        }
        return false;
    }

    public synchronized static boolean write(){
        logger.finer("Started config write.");
        try{
            final YamlWriter OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            OUT.close();
            logger.finer("Finished config write.");
            return true;
        }catch(final IOException e){
            logger.severe(
                "Failed to write to configuration file" +
                (e instanceof YamlException ? " (Invalid syntax)" : "") + ".\n" +
                Logger.getStackTraceAsString(e)
            );
            return false;
        }
    }

}
