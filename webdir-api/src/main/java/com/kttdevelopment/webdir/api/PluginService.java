package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.io.*;
import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

public abstract class PluginService {

    public abstract Logger getLogger();

    // server reference

    public abstract SimpleHttpServer getHttpServer();

    // local config

    public abstract ConfigurationFile createConfiguration();

    public abstract ConfigurationFile createConfiguration(final File configFile);

    public abstract ConfigurationFile createConfiguration(final Reader reader);

    public abstract ConfigurationFile createConfiguration(final InputStream stream);

    // local locale

    public abstract LocaleBundle getLocale();

    // local permissions

    public abstract boolean hasPermission(final String permission);

    public abstract boolean hasPermission(final InetAddress address, final String permission);

    // local folder

    public abstract File getPluginFolder();

    // internal

    public abstract String getPluginName();

    public abstract String getVersion();

    public abstract String getAuthor();

    public abstract List<String> getAuthors();

    public abstract Class<WebDirPlugin> getMainClass();

    //

    public abstract InputStream getResource(final String path);

}
