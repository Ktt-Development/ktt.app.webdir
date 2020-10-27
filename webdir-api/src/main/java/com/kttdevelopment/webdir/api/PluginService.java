package com.kttdevelopment.webdir.api;

import java.io.File;
import java.util.logging.Logger;

public abstract class PluginService {

    abstract Logger getLogger();

    abstract File getPluginFolder();

    abstract WebDirPlugin getPlugin(final String pluginName);

    abstract <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass);

    abstract LocaleBundle getLocaleBundle(final String resource);

    abstract File getSourcesFolder();

    abstract File getOutputFolder();

    abstract File getDefaultsFolder();

    abstract File getPluginsFolder();

}
