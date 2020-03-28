package com.kttdevelopment.webdir.main;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class Config {

    private static String resourceFolder = "/config";

    private static File configFile = new File(com.kttdevelopment.webdir.main.Main.root + "config.yml");
    private static Map config;

    private static File defaultConfigFile = new File(Config.class.getClassLoader().getResource(resourceFolder.substring(1) + "/" + "config.yml").getFile());
    private static Map defaultConfig;

    abstract static class Main {

        synchronized static void init(){
            try{

                final YamlReader IN = new YamlReader(new FileReader(defaultConfigFile));
                defaultConfig = (Map) IN.read();
            }catch(final FileNotFoundException | YamlException e){ // should never occur in production
                Logger.logger.severe(
                    "Failed to load default configuration file" +
                    (e instanceof YamlException ? "(Invalid syntax)" : "") + ".\n" +
                    (e instanceof YamlException ? Logger.getStackTraceAsString(e) : "")
                );
                throw new RuntimeException(e);
            }
            read();
        }
    }

    public synchronized static boolean read(){
        try{
            final YamlReader IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            return true;
        }catch(final FileNotFoundException e){
            Logger.logger.warning("Configuration file not found, creating new configuration file.");
            config = defaultConfig;
            if(!write())
                Logger.logger.severe("Failed to create configuration file, using default configuration file.");
            else
                Logger.logger.info("New configuration file created.");
        }catch(final YamlException e){
            Logger.logger.warning("Configuration file syntax is incorrect, using default configuration file." + "\n" + Logger.getStackTraceAsString(e));
            config = defaultConfig;
        }
        return false;
    }

    public synchronized static boolean write(){
        try{
            final YamlWriter OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            OUT.close();
            return true;
        }catch(final IOException e){
            Logger.logger.severe(
                "Failed to write to configuration file" +
                (e instanceof YamlException ? " (Invalid syntax)" : "") + ".\n" +
                Logger.getStackTraceAsString(e)
            );
            return false;
        }
    }

}
