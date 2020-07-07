package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.server.config.ConfigurationFileImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Logger;

public final class ConfigService {

    private final ConfigurationFile config;

    public final ConfigurationFile getConfig(){
        return config;
    }
    
    private final File configFile;
    private final String defaultConfig;

    //

    ConfigService(final File configFile, final String defaultConfig) throws IOException{
        this.configFile = configFile;
        this.defaultConfig = defaultConfig;

        Logger logger = Logger.getLogger("Config");

        logger.info("Started config initialization");

        // load default
        final ConfigurationFile def;
        try(final InputStream IN = ConfigService.class.getResourceAsStream(defaultConfig)){
            final ConfigurationFileImpl impl = new ConfigurationFileImpl();
            impl.load(IN);
            def = impl;
        }catch(final ClassCastException | IOException e){
            logger.severe(
                (e instanceof IOException ? "Failed to load default configuration file" : "Failed to load default configuration file (invalid syntax)") + '\n' + LoggerService.getStackTraceAsString(e)
            );
            throw e;
        }

        // load config
        ConfigurationFile tConfig = new ConfigurationFileImpl(configFile);

        try{
            final ConfigurationFileImpl impl = new ConfigurationFileImpl();
            impl.load(configFile);
            tConfig = impl;

            logger.info("Finished loading configuration file");
        }catch(final FileNotFoundException ignored){
            logger.warning("Configuration file not found, creating a new configuration file");
            copyDefaultConfig();
        }catch(final ClassCastException | YamlException e){
            logger.severe(
                "Failed to read configuration file (invalid syntax), using default configuration" +
                '\n' + LoggerService.getStackTraceAsString(e)
            );
        }

        //

        tConfig.setDefault(def);
        config = tConfig;

        final LocaleService locale = Application.getLocaleService();
        locale.setLocale(config.getString("locale","en"));

        logger = Logger.getLogger(locale.getString("config"));

        logger.info(locale.getString("config.init.finished"));
    }

    // method only creates a file if it doesn't exist. It does not load the default config
    public final synchronized void copyDefaultConfig(){
        final LocaleService locale = Application.getLocaleService();
        final Logger logger = Logger.getLogger(locale.getString("config"));

        logger.info(locale.getString("config.copyDefaultConfig.start"));

        if(!configFile.exists()){
            try(final InputStream IN = ConfigService.class.getClassLoader().getResourceAsStream(defaultConfig)){
                Files.copy(Objects.requireNonNull(IN),configFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                logger.info(locale.getString("config.copyDefaultConfig.finished"));
            }catch(final NullPointerException ignored){
                logger.severe(locale.getString("config.copyDefaultConfig.notFound"));
            }catch(final IOException e){
                logger.severe(locale.getString("config.copyDefaultConfig.io") + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }
    }

    // attempts to reload the config from the file
    public final synchronized void load(){
        final LocaleService locale = Application.getLocaleService();
        final Logger logger = Logger.getLogger(locale.getString("config"));

        logger.info(locale.getString("config.load.start"));
        config.reload();
        logger.info(locale.getString("config.load.finished"));
    }

    // saves the config to the file
    public final synchronized void save(){
        final LocaleService locale = Application.getLocaleService();
        final Logger logger = Logger.getLogger(locale.getString("config"));

        logger.info(locale.getString("config.save.start"));
        config.save();
        logger.info(locale.getString("config.save.finished"));
    }

}
