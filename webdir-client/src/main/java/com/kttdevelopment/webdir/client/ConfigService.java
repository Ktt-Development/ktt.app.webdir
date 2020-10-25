package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.client.config.Setting;

import java.io.*;
import java.util.*;

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
        CONTEXT     = "context",
        PERMISSIONS = "permissions";

    private static final Setting[] settings = new Setting[]{
        new Setting(SAFE, String.valueOf(false), "Safe mode disables plugin loading."),
        new Setting(LANG, "en_US", "The Language that the application will use for logging."),
        new Setting(PLUGINS, "_plugins", "The folder where plugins will be loaded from."),
        new Setting(SOURCES, "_root", "The folder where files will be loaded from."),
        new Setting(DEFAULT, "_default", "The folder where defaults will be loaded from."),
        new Setting(OUTPUT, "_site", "The folder where file renders will be saved."),
        new Setting(CLEAN, String.valueOf(true), "Whether to clear the output directory before rendering files."),
        new Setting(SERVER, String.valueOf(false), "Whether to run a server or not."),
        new Setting(PORT, String.valueOf(80), "The port to run the server at."),
        new Setting(CONTEXT, "files", "The context to view files at.\\nEx: setting this to 'files' would put files from C://* at http://localhost/files/C:/*"),
        new Setting(PERMISSIONS, "permissions.yml", "The file to load permissions from (server only).")
    };

    //

    private final File configFile;
    private final YamlMapping configuration;

    ConfigService(final File configFile) throws IOException{
        final LoggerService loggerService = Main.getLogger();
        final String loggerName = "Configuration Service";

        // todo: init message

        this.configFile = Objects.requireNonNull(configFile);

        // load default configuration
        final YamlMapping defaultConfig;
        final String defaultYaml;
        {
            // todo: loading def message

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
                defaultConfig = Yaml.createYamlInput(defaultYaml).readYamlMapping();
            }catch(final IOException e){
                // todo: severe message (this should never happen!)
                throw e;
            }

            // todo: finished def message
        }

        // load configuration
        {
            // todo: loading config message

            YamlMapping yaml = null;
            try{
                yaml = Yaml.createYamlInput(configFile).readYamlMapping();
            }catch(final IOException e){
                // todo: e instanceof file not found
                e.printStackTrace();
                // todo: message
            }
            configuration = yaml == null ? defaultConfig: yaml;

            // todo: finish config message
        }

    }

}
