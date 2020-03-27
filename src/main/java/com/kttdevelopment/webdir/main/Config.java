package com.kttdevelopment.webdir.main;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;
import java.net.URL;
import java.util.Map;

import static com.kttdevelopment.webdir.main._vars.*;

public abstract class Config {

    abstract static class Main {

        synchronized static void init(){
            YamlReader IN = null;
            try{
                IN = new YamlReader(new FileReader(root + "/" + config.config));
            }catch(final FileNotFoundException ignored){
                logger.logger.severe(locale.bundle.get(locale.loadedLocale).getString("logging.configFileMissingUseDef"));
                loadDefaultConfig();
            }
            try{
                config.loadedConfig = (Map) IN.read();
            }catch(final YamlException ignored){
                logger.logger.severe(locale.bundle.get(locale.loadedLocale).getString("logging.configSyntaxIncorrect"));
                loadDefaultConfig();
            }
        }

        private static void loadDefaultConfig(){
            try{
                final YamlReader IN = new YamlReader(new FileReader(Main.class.getClassLoader().getResource(config.resource + "/" + config.config).getPath()));
                config.loadedConfig = (Map) IN.read();
            }catch(final FileNotFoundException | YamlException ignored){ /* Should never occur */ }
        }

    }

}
