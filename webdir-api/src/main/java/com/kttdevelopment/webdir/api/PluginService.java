package com.kttdevelopment.webdir.api;

import java.io.File;
import java.util.logging.Logger;

public abstract class PluginService {

    public abstract String getPluginName();

    public abstract Logger getLogger();

    public abstract File getPluginFolder();

    public abstract WebDirPlugin getPlugin(final String pluginName);

    public abstract <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass);

    public abstract LocaleBundle getLocaleBundle(final String resource, final ClassLoader classLoader);

    public abstract File getSourcesFolder();

    public abstract File getOutputFolder();

    public abstract File getDefaultsFolder();

    public abstract File getPluginsFolder();

}
