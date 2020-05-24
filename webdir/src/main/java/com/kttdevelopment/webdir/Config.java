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

    public synchronized static boolean read(){
        logger.info("[Config] Loading config from file"); // add locale nonNullElse

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
            logger.info("[Config] Finished loading config from file");
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning("[Config] Config file not found, creating new config file");
            config = defaultConfig;
            if(!write())
                logger.severe("[Config] Failed to create config file, using default config");
            else
                logger.info("[Config] New config file created");
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                "[Config] Failed to read config file" +
                (e instanceof YamlException ? ' ' + "(invalid syntax)" + '\n' + getStackTraceAsString(e) : "")
            );
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.severe("[Config] Failed to  close config input stream" + '\n' + getStackTraceAsString(e));
                }
        }
        return false;
    }

    public synchronized static boolean write(){
        logger.info("[Config] Writing config to file"); // add locale nonNullElse

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(config);
            logger.info("[Config] Finished writing config to file");
            return true;
        }catch(final IOException e){
            logger.severe("[Config] Failed to write config to file" + '\n' + getStackTraceAsString(e));
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe("[Config] Failed to close config output stream" + '\n' + getStackTraceAsString(e));
                }
        }
        return false;
    }

}
