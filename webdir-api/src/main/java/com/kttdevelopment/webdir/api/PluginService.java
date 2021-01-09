/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.api;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class implements the methods required by the {@link WebDirPlugin}. Plugin developers do not use this class.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Ktt Development
 */
public abstract class PluginService {

    public abstract String getPluginName();

    public abstract Logger getLogger();

    public abstract File getPluginFolder();

    public abstract Map<String,? super Object> getPluginYml();

    public abstract Map<String,? super Object> getConfigYml();

    public abstract WebDirPlugin getPlugin(final String pluginName);

    public abstract <T extends WebDirPlugin> T getPlugin(final String pluginName, final Class<T> pluginClass);

    public abstract LocaleBundle getLocaleBundle(final String resource, final ClassLoader classLoader);

    public abstract File getSourcesFolder();

    public abstract File getOutputFolder();

    public abstract File getDefaultsFolder();

    public abstract File getPluginsFolder();

}
