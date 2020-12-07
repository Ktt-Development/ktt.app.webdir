package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.config.Setting;
import com.kttdevelopment.webdir.client.utility.MapUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public final class ConfigService {

    public static final String
        SAFE        = "safe",
        LANG        = "lang",
        PLUGINS     = "plugins",
        SOURCES     = "sources",
        DEFAULT     = "default",
        OUTPUT      = "output",
        CLEAN       = "clean",
        SERVER      = "server",
        PORT        = "port",
        RAW         = "raw",
        CONTEXT     = "context",
        F04         = "404",
        PERMISSIONS = "permissions";

    private static final Setting[] settings = new Setting[]{
        new Setting(SAFE, String.valueOf(false), "Safe mode disables plugin loading."),
        new Setting(LANG, Locale.getDefault().getLanguage() + '_' + Locale.getDefault().getCountry(), "The Language that the application will use for logging."),
        new Setting(PLUGINS, "_plugins", "The folder where plugins will be loaded from."),
        new Setting(SOURCES, "_root", "The folder where files will be loaded from."),
        new Setting(DEFAULT, "_default", "The folder where defaults will be loaded from."),
        new Setting(OUTPUT, "_site", "The folder where file renders will be saved."),
        new Setting(CLEAN, String.valueOf(true), "Whether to clear the output directory before rendering files."),
        new Setting(SERVER, String.valueOf(false), "Whether to run a server or not."),
        new Setting(PORT, String.valueOf(80), "The port to run the server at."),
        new Setting(RAW, "raw", "The context to view raw files at.\nEx: setting this to 'raw' would put files from C://* at http://localhost/raw/C:/*"),
        new Setting(CONTEXT, "files", "The context to view files at.\nEx: setting this to 'files' would put files from C://* at http://localhost/files/C:/*"),
        new Setting(F04, "404.html", "The file to use as the 404 page."),
        new Setting(PERMISSIONS, "permissions.yml", "The file to load permissions from (server only).")
    };

    //

    private final File configFile;
    private final Map<String,Object> configuration;

    public final Map<String,Object> getConfiguration(){
        return configuration;
    }

    ConfigService(final File configFile){
        final LoggerService loggerService = Main.getLogger();
        final String loggerName = "Configuration Service";
        final String fileName = configFile.getPath();

        loggerService.addQueuedLoggerMessage(
            "config.name", "config.constructor.start",
            loggerName, "Started configuration service initialization.",
            Level.INFO
        );

        this.configFile = Objects.requireNonNull(configFile);

        // load default configuration
        final Map<String,Object> defaultConfig;
        final String defaultYaml;
        {
            loggerService.addQueuedLoggerMessage(
                "config.name", "config.constructor.default.start",
                loggerName, "Loading default configuration.",
                Level.FINE
            );

            final StringBuilder defaultYamlBuilder = new StringBuilder();
            defaultYamlBuilder.append( // header
                "############################################################\n" +
                "# +------------------------------------------------------+ #\n" +
                "# |                 WebDir Configuration                 | #\n" +
                "# +------------------------------------------------------+ #\n" +
                "############################################################");
            for(final Setting setting : settings)
                defaultYamlBuilder.append("\n\n").append(setting.getYaml());

            defaultYaml = defaultYamlBuilder.toString();
            try{
                defaultConfig = MapUtility.asStringObjectMap(new Yaml().load(defaultYaml));
            }catch(final ClassCastException | YAMLException e){
                loggerService.addQueuedLoggerMessage(
                    "config.name", "config.constructor.default.fail",
                    loggerName, "Failed to load default configuration.",
                    Level.SEVERE, LoggerService.getStackTraceAsString(e)
                );
                throw e;
            }

            loggerService.addQueuedLoggerMessage(
                "config.name", "config.constructor.default.finish",
                loggerName, "Loaded default configuration.",
                Level.FINE
            );
        }

        // load configuration
        {
            loggerService.addQueuedLoggerMessage(
                "config.name", "config.constructor.config.start",
                loggerName, "Loading configuration from file %s.",
                Level.INFO, fileName
            );

            Map<String,Object> yaml = null;
            try(final FileInputStream IN = new FileInputStream(configFile)){
                yaml = new Yaml().load(IN);
            }catch(final ClassCastException | YAMLException | IOException e){
                loggerService.addQueuedLoggerMessage(
                    "config.name", "config.constructor.config." + (e instanceof FileNotFoundException ? "missing" : "malformed"),
                    loggerName, e instanceof FileNotFoundException ? "Failed to load configuration from file %s (file not found). Using default configuration. %s" : "Failed to load configuration from file %s (malformed yaml). Using default configuration. %s",
                    Level.INFO, fileName, LoggerService.getStackTraceAsString(e)
                );
                // copy default if missing
                if(!configFile.exists())
                    try{
                        Files.write(configFile.toPath(), defaultYaml.getBytes(StandardCharsets.UTF_8));
                            loggerService.addQueuedLoggerMessage(
                                "config.name", "config.constructor.config.default.success",
                                loggerName, "Created default configuration file at %s.",
                                Level.INFO, fileName
                            );
                    }catch(final IOException | SecurityException e2){
                        loggerService.addQueuedLoggerMessage(
                            "config.name", "config.constructor.config.default.fail",
                            loggerName, "Failed to create default configuration file at %s. %s",
                            Level.SEVERE, fileName, LoggerService.getStackTraceAsString(e2)
                        );
                    }
            }

            // populate with defaults
            if(yaml == null)
                configuration = defaultConfig;
            else{
                Map<String,Object> map = new HashMap<>();
                for(final Setting setting : settings){
                    final String key = setting.getKey();
                    map.put(key, yaml.getOrDefault(key, defaultConfig.get(key)).toString());
                }
                configuration = map;
            }

            loggerService.addQueuedLoggerMessage(
                "config.name", "config.constructor.config.finish",
                loggerName, "Loaded configuration.",
                Level.INFO
            );
        }

        loggerService.addQueuedLoggerMessage(
            "config.name", "config.constructor.finish",
            loggerName, "Finished configuration service initialization.",
            Level.INFO
        );
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("configFile", configFile)
            .addObject("configuration", configuration)
            .toString();
    }

}
