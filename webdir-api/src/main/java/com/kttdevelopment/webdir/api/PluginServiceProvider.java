package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.util.logging.Logger;

public abstract class PluginServiceProvider {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static Logger getLogger(){
            return logger;
        }

    // application reference

    public abstract SimpleHttpServer getHttpServer();

    // local config

    // local locale

}
