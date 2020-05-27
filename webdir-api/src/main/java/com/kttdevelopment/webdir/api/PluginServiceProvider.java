package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.net.InetAddress;
import java.util.logging.Logger;

public abstract class PluginServiceProvider {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static Logger getLogger(){
            return logger;
        }

    // application reference

    public abstract SimpleHttpServer getHttpServer();

    // local config

    public abstract ConfigurationFile getConfiguration();

    // local locale

    public abstract LocaleBundle getLocale();

    // local permissions

    public abstract boolean hasPermission(final String permission);

    public abstract boolean hasPermission(final InetAddress address, final String permission);

}
