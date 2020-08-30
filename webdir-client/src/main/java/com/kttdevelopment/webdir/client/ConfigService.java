package com.kttdevelopment.webdir.client;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.config.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigService {

    private static final Setting<?>[] settings = new Setting[]{
        new Setting<>("safe"        ,false      ,"Plugins will not load in safe mode."),
        new Setting<>("lang", "en_US"    , "The language that the application will use for logging and plugins."),
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

    private final File configFile;
    private final ConfigurationSection config;

    public final ConfigurationSection getConfig(){
        return new ConfigurationSectionImpl(config.toMapWithDefaults());
    }

    ConfigService(final File configFile){
        final LoggerService loggerService   = Main.getLoggerService();
        final Logger logger                 = loggerService.getLogger("Configuration Service");

        loggerService.addQueuedLoggerMessage(
            "configService", "configService.const.started",
            logger.getName(), "Started configuration service initialization",
            Level.INFO
        );

        this.configFile = Objects.requireNonNull(configFile);

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
            defaultYamlBuilder.append( // header
                "############################################################\n" +
                "# +------------------------------------------------------+ #\n" +
                "# |                 WebDir Configuration                 | #\n" +
                "# +------------------------------------------------------+ #\n" +
                "############################################################");
            for(final Setting<?> setting : settings){
                defaultYamlBuilder.append("\n\n").append(setting.getYaml());
                defaultConfiguration.put(setting.getKey(),setting.getDefaultValue());
            }

            defaultYaml = defaultYamlBuilder.toString();
        }
        loggerService.addQueuedLoggerMessage(
            "configService", "configService.const.loadedDefault",
            logger.getName(), "Loaded default configuration \n%s\n",
            Level.FINE,defaultYaml
        );

        // load configuration
        final ConfigurationFile config = new ConfigurationFile();
        {
            try{
                loggerService.addQueuedLoggerMessage(
                    "configService","configService.const.loadConfig",
                    logger.getName(),"Loading configuration from %s",
                    Level.FINER,configFile
                );
                config.load(configFile);
            }catch(final FileNotFoundException e){
                loggerService.addQueuedLoggerMessage(
                    "configService","configService.const.configNotFound",
                    logger.getName(),"Configuration file not found, creating a new configuration file",
                    Level.WARNING
                );
                // create default file
                if(!configFile.exists())
                    try{
                        if(!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs())
                            loggerService.addQueuedLoggerMessage(
                                "configService","configService.const.failedCreateDirs",
                                logger.getName(),"Failed to create parent directories for %s",
                                Level.SEVERE
                            );
                        else
                            Files.write(configFile.toPath(),defaultYaml.getBytes(StandardCharsets.UTF_8));
                    }catch(final IOException e2){
                        loggerService.addQueuedLoggerMessage(
                            "configService","configService.const.failedCreateDefault",
                            logger.getName(),"Failed to create default configuration file\n%s",
                            Level.SEVERE,LoggerService.getStackTraceAsString(e2)
                        );
                    }
                else
                    loggerService.addQueuedLoggerMessage(
                        "configService","configService.const.configAlreadyExists",
                        logger.getName(),"Failed to create default configuration file (file already exists)\n %s",
                        Level.SEVERE,LoggerService.getStackTraceAsString(e)
                    );
            }catch(final ClassCastException | YamlException e){
                loggerService.addQueuedLoggerMessage(
                    "configService","configService.const.invalidConfigSyntax",
                    logger.getName(),"Failed to load configuration file (invalid syntax)\n %s",
                    Level.WARNING,LoggerService.getStackTraceAsString(e)
                );
            }
        }
        config.setDefault(new ConfigurationSectionImpl(defaultConfiguration));

        this.config = config;

        loggerService.addQueuedLoggerMessage(
            "configService","configService.const.finished",
            logger.getName(),"Finished configuration service initialization",
            Level.INFO
        );
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultConfiguration",settings)
            .addObject("configurationFile",configFile)
            .addObject("configuration",config)
            .toString();
    }

}
