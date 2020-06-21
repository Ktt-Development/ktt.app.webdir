package com.kttdevelopment.webdir.pluginservice;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.serviceprovider.*;
import com.kttdevelopment.webdir.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.config.SafeConfigurationFileImpl;
import com.kttdevelopment.webdir.httpserver.SimpleHttpServerUnmodifiable;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;

public class PluginServiceImpl extends PluginService {

    private final Class<WebDirPlugin> mainClass;

    private final Logger logger;
    private final SimpleHttpServer server = new SimpleHttpServerUnmodifiable(Application.server.getServer());
    private final LocaleBundle locale;
    private final String pluginName, version;
    private final List<String> authors;
    private final File folder;

    private final String folderPath; // todo

    public PluginServiceImpl(final Class<WebDirPlugin> mainClass, final ConfigurationSection yml){
        this.mainClass = mainClass;

        locale = new LocaleBundleImpl();
        pluginName = Objects.requireNonNull(yml.getString("name"));
        version = yml.getString("version");
        folder = new File(Application.parent + '\\' + "plugins" + '\\' + pluginName.replaceAll("[\\\\/:*?\"<>|]","_"));
        authors = yml.getList("authors",String.class);

        logger = Logger.getLogger(pluginName);

        folderPath = folder.getAbsolutePath();
    }

    @Override
    public final Logger getLogger(){
        return logger;
    }

    @Override
    public final SimpleHttpServer getHttpServer(){
        return server;
    }

    @Override
    public final ConfigurationFile createConfiguration(){
        try{ return new SafeConfigurationFileImpl();
        }catch(final Exception ignored){ return null; }
    }

    @Override
    public final ConfigurationFile createConfiguration(final File configFile){
        try{ return new SafeConfigurationFileImpl(configFile);
        }catch(final Exception ignored){ return null; }
    }

    @Override
    public final ConfigurationFile createConfiguration(final Reader reader){
        try{
            return new ConfigurationFileImpl(reader);
        }catch(final IOException ignored){
            return null;
        }
    }

    @Override
    public final ConfigurationFile createConfiguration(final InputStream stream){
        try{
            return new ConfigurationFileImpl(stream);
        }catch(final IOException ignored){
            return null;
        }
    }

    @Override
    public final LocaleBundle getLocale(){
        return locale;
    }

    @Override
    public final boolean hasPermission(final String permission){
        return hasPermission(null,permission);
    }

    @Override
    public final boolean hasPermission(final InetAddress address, final String permission){
        return Application.permissions.getPermissions().hasPermission(address,permission);
    }

    @Override
    public final File getPluginFolder(){
        return (!folder.exists() && !folder.mkdir()) ? null : folder;
    }

    @Override
    public final String getPluginName(){
        return pluginName;
    }

    @Override
    public final String getVersion(){
        return version;
    }

    @Override
    public final String getAuthor(){
        return authors.size() >= 1 ? authors.get(0) : null;
    }

    @Override
    public final List<String> getAuthors(){
        return authors;
    }

    @Override
    public final Class<WebDirPlugin> getMainClass(){
        return mainClass;
    }

    @Override
    public final InputStream getResource(final String path){
        if (path == null)
            throw new IllegalArgumentException("Filename cannot be null");
        return mainClass.getClassLoader().getResourceAsStream(path);
    }

}
