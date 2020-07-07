package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public abstract class PluginService {

    public abstract Logger getLogger();

    public abstract File getPluginFolder();

    public abstract PluginYml getPluginYml();

    public abstract InputStream getResource(final String path);

    public abstract ConfigurationFile createConfiguration();

}
