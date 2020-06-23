package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.io.*;
import java.net.InetAddress;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * API Implementation for WebDir Plugins
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public abstract class PluginService {

    /**
     * Returns the logger reserved for the plugin.
     *
     * @return logger
     *
     * @see Logger
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract Logger getLogger();

// server reference

    /**
     * Returns the http server running the plugin.
     *
     * @return unmodifiable http server
     *
     * @see SimpleHttpServer
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract SimpleHttpServer getHttpServer();

// local config

    /**
     * Creates a non-reference configuration file.
     *
     * @return configuration file
     *
     * @see ConfigurationFile
     * @see #createConfiguration(File)
     * @see #createConfiguration(InputStream)
     * @see #createConfiguration(Reader)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract ConfigurationFile createConfiguration();

    /**
     * Creates a configuration from a file.
     *
     * @param configFile config file
     * @return configuration file
     *
     * @see ConfigurationFile
     * @see #createConfiguration()
     * @see #createConfiguration(InputStream)
     * @see #createConfiguration(Reader)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract ConfigurationFile createConfiguration(final File configFile);

    /**
     * Creates a non-reference configuration file from a input stream.
     *
     * @param stream input stream
     * @return configuration file
     *
     * @see ConfigurationFile
     * @see #createConfiguration()
     * @see #createConfiguration(File)
     * @see #createConfiguration(Reader)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract ConfigurationFile createConfiguration(final InputStream stream);

    /**
     * Creates a non-reference configuration file from a reader.
     *
     * @param reader reader
     * @return configuration file
     *
     * @see ConfigurationFile
     * @see #createConfiguration()
     * @see #createConfiguration(File)
     * @see #createConfiguration(InputStream)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract ConfigurationFile createConfiguration(final Reader reader);

// local locale

    /**
     * Returns the locale bundle for the application.
     *
     * @return locale bundle
     *
     * @see LocaleBundle
     * @see LocaleBundle#addLocale(ResourceBundle)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract LocaleBundle getLocale();

// local permissions

    /**
     * Returns if a default client has a permission.
     *
     * @param permission permission to check
     * @return if client has permission
     *
     * @see #hasPermission(InetAddress, String)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract boolean hasPermission(final String permission);

    public abstract boolean hasPermission(final InetAddress address, final String permission);

// local folder

    /**
     * Returns the plugin folder reserved for this specific plugin. This is where you can store data and configuration files for your plugin.
     *
     * @return plugin folder
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract File getPluginFolder();

// internal

    /**
     * Returns the <code>pluginName</code> value from the plugin.yml
     * <br><i>required</i>
     * @return plugin name
     *
     * @since 01.00.00
     */
    public abstract String getPluginName();

    /**
     * Returns the <code>pluginVersion</code> value from the plugin.yml
     * @return plugin version
     *
     * @since 01.00.00
     */
    public abstract String getVersion();

    /**
     * Returns the first <code>author</code> value from the plugin.yml
     * @return first author
     *
     * @see #getAuthors()
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String getAuthor();

    /**
     * Returns the <code>author</code> value from the plugin.yml
     * @return authors
     *
     * @see #getAuthor()
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract List<String> getAuthors();

    /**
     * Returns the <code>mainClass</code> value from the plugin.yml
     * <br> <i>required</i>
     *
     * @return main class
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract Class<WebDirPlugin> getMainClass();

//

    /**
     * Returns a resource from you plugin's resource folder.
     *
     * @param path resource path
     * @return resource as input stream
     *
     * @see ClassLoader#getResourceAsStream(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract InputStream getResource(final String path);

}
