package com.kttdevelopment.webdir.api.serviceprovider;

/**
 * A locale bundle is used to localize your plugin by returning different string depending on the currently selected language.
 * <br><br>
 *
 * To create a locale bundle use {@link com.kttdevelopment.webdir.api.WebDirPlugin#getLocaleBundle(String)} where the parameter is the resource prefix. Note that the resource name does not include the language/country code.
 * <br>
 * <b>Example:</b> A bundle file with the name <code>bundle_en_US.properties</code> would have a resource name of <code>bundle</code>.
 * <br><br>
 *
 * Language bundles will always look for the nearest matching locale file available. If none can be found for the currently set language then it will fallback to the application default of English (United States).
 * <br><br>
 *
 * If a locale file is not found for the currently selected language it will attempt to find the closest match by language code and region, before using the closest default locale file.
 * <br>
 * <pre>
 * bundle_jp_JA_UNIX.properties
 * bundle_jp_JA.properties
 * bundle_jp.properties
 * bundle_en_US.properties
 * bundle_en.properties
 * bundle.properties
 * </pre>
 *
 * @since 01.00.00
 * @version 01.00.00
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
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final String key);

    /**
     * Returns the string formatted localized value for a key or just the localized value if there are insufficient parameters. Same as <code>String.format(getString(String),Object[]...)</code>.
     *
     * @param key key
     * @param args arguments
     * @return string formatted localized string or unformatted localized string if insufficient parameters
     *
     * @see #getString(String)
     * @see String#format(String, Object...)
     * @since 01.00.00
     * @author Ktt Development
     */
    @SuppressWarnings("SpellCheckingInspection")
    String getString(final String key, final Object... args);

}
