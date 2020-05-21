package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;

import static com.kttdevelopment.webdir.Logger.*;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class Config {

    private static final File configFile = new File("");
    private static Map config;

    @SuppressWarnings("ConstantConditions")
    private static final File resource = new File(Config.class.getClassLoader().getResource("config/config.yml").getFile());
    private static Map defaultConfig;

    public static Object get(final String key){
        // debug
        final Object value =  config.get(key);
        if(value == null){
            final Object def = defaultConfig.get(key);
            // logger
            config.put(key,def);
            return def;
        }
        // logger
        return value;
    }

    public synchronized static void set(final String key, final Object value){
        config.put(key,value);
        // logger
        write();
    }

    //

    private static boolean init;

    public synchronized static void main(){
        if(init) return; init = true;

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(resource));
            defaultConfig = (Map) IN.read();
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe(
                "Failed to load default configuration file" +
                (e instanceof YamlException ? ' ' + "(Invalid syntax)" + '\n' + Logger.getStackTraceAsString(e) : "")
            );
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException ignored){ }
        }

        read();
        logger.info("Finished config init");
        Locale.setLocale(new java.util.Locale(Config.get("lang").toString()));
    }

    //

    @SuppressWarnings("UnusedReturnValue")
    private synchronized static boolean read(){
        logger.finer("Started config read");

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.finer("Finished config read");
            return true;
        }catch(final FileNotFoundException e){
            logger.warning("Configuration file not found, creating new configuration file");
            config = defaultConfig;
            if(!write())
                logger.severe("Failed to create configuration file, using default configuration file");
            else
                logger.info("New configuration file created");
        }catch(final ClassCastException | YamlException e){
            logger.severe("Configuration file syntax is incorrect, using default configuration file" + '\n' + Logger.getStackTraceAsString(e));
            config = defaultConfig;
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException ignored){}
        }
        return false;
    }

    public synchronized static boolean write(){
        // start log

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            // end log
            return true;
        }catch(final IOException e){
            // logger syntax
        }finally{
            if(OUT != null)
                try{OUT.close();
                }catch(final YamlException ignored){ }
        }
        return false;
    }

}
