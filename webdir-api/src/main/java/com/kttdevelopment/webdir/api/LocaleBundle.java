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

/**
 * A locale bundle is used to localize your plugin by returning different strings depending on the currently selected language.
 * <br><br>
 *
 * To create a locale bundle use {@link WebDirPlugin#getLocaleBundle(String)} where the parameter is the resource prefix.
 * Note that the resource name does not include the language/country code.
 * <br>
 * <b>Example:</b> A bundle file with the name <code>bundle_en_US.yml</code> would have a resource name of <code>bundle</code>.
 * <br><br>
 *
 * Language bundles will always look for the nearest matching locale file available.
 * If a locale file is not found for the currently selected language it will attempt to find the closest match by language code and region, before using the closest default locale file.
 * <br>
 * <pre>
 * bundle_jp_JA.yml
 * bundle_jp.yml
 * bundle_en_US.yml
 * bundle_en.yml
 * bundle.yml
 * </pre>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Ktt Development
 */
public interface LocaleBundle {

    /**
     * Returns the localized value for a key.
     *
     * @param key key
     * @return localized string
     *
     * @see #getString(String, Object...)
     * @since 1.0.0
     * @author Ktt Development
     */
    String getString(final String key);

    /**
     * Returns the string formatted localized value for a key or just the localized value if there are insufficient parameters.
     *
     * @param key key
     * @param args arguments
     * @return formatted localized string
     *
     * @see String#format(String, Object...)
     * @see #getString(String)
     * @since 1.0.0
     * @author Ktt Development
     */
    String getString(final String key, final Object... args);

}
