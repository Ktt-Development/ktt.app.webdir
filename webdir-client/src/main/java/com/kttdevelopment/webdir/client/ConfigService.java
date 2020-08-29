package com.kttdevelopment.webdir.client;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.client.config.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigService {

    private final File configFile;

    private final Setting<?>[] settings = new Setting[]{
        new Setting<>("safe"        ,false      ,"Plugins will not load in safe-mode."),
        new Setting<>("locale"      ,"en_US"    ,"The language that the application will use for logging and plugins."),
        new Setting<>("plugins_dir" ,".plugins" ,"The folder where plugins will be loaded from."),
        new Setting<>("sources_dir" ,".root"    ,"The folder where files will be loaded from."),
        new Setting<>("default_dir" ,".default" ,"The folder where default configurations will be loaded from."),
        new Setting<>("output_dir"  ,"_site"    ,"The folder where file renders will be saved."),
        new Setting<>("clean"       ,true       ,"Whether to clear the output directory before rendering files."),
        new Setting<>("server"      ,false      ,"Whether to start a server or not."),
        new Setting<>("port"        ,80         ,"The port to run the server at."),
        new Setting<>("file_context","files"    ,"The context to view files at.\nEx: setting this to 'files' would put files from C://* at http://localhost/files/C:/*"),
        new Setting<>("permissions"   ,"permissions.yml","The permissions file.")
    };

    public ConfigService(final File configFile){
        final LoggerService loggerService = Main.getLoggerService();
        final Logger logger = loggerService.getLogger("Configuration Service");

        loggerService.addQueuedLoggerMessage(
            "configService", "configService.const.started",
            logger.getName(), "Started configuration service initialization",
            Level.INFO
        );

        Objects.requireNonNull(configFile);
        this.configFile = configFile;

        // default configuration
        loggerService.addQueuedLoggerMessage(
            "configService", "configService.const.loadDefault",
            logger.getName(), "Loading default configuration",
            Level.FINE
        );

        final Map<String,Object> defaultConfiguration = new LinkedHashMap<>();
        final String defaultYaml;
        {
            final StringBuilder defaultYamlBuilder = new StringBuilder();
            defaultYamlBuilder.append(
                "############################################################\n" +
                "# +------------------------------------------------------+ #\n" +
                "# |                 WebDir Configuration                 | #\n" +
                "# +------------------------------------------------------+ #\n" +
                "############################################################");
            for(final Setting<?> setting : settings){
                defaultYamlBuilder.append('\n').append(setting.getYaml());
                defaultConfiguration.put(setting.getKey(),setting.getDefaultValue());
            }

            defaultYaml = defaultYamlBuilder.toString();
        }
        loggerService.addQueuedLoggerMessage(
            "configService", "configService.const.loadedDefault",
            logger.getName(), "Loaded default configuration \n %s \n",
            Level.FINE,defaultYaml
        );

        // load configuration
        final ConfigurationFile config = new ConfigurationFile();
        {
            final Runnable createNewDefault = () -> {

            };

            try{
                loggerService.addQueuedLoggerMessage(
                    "configService","configService.const.loadConfig",
                    logger.getName(),"Loading configuration file from %s",
                    Level.FINER,configFile
                );
                if(!configFile.exists()){
                
                }
                else if(!configFile.canRead())
                    throw new IOException();
                else
                    config.load(configFile);
            }catch(YamlException e){
                e.printStackTrace();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        config.setDefault(new ConfigurationSectionImpl(defaultConfiguration));


    }

}
