package com.kttdevelopment.webdir.api.serviceprovider;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Handles locale strings for the plugin.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface LocaleBundle {

    /**
     * Adds a bundle to the locale.
     *
     * @param bundle bundle to add
     *
     * @see ResourceBundle
     * @since 01.00.00
     * @author Ktt Development
     */
    void addLocale(final ResourceBundle bundle);

    /**
     * Returns if a bundle exists for a locale.
     *
     * @param locale locale to check
     * @return if bundle exists for locale
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean hasLocale(final Locale locale);

    /**
     * Returns the current locale.
     *
     * @return current locale
     *
     * @see #setLocale(Locale)
     * @since 01.00.00
     * @author Ktt Development
     */
    Locale getLocale();

    /**
     * Sets the locale.
     *
     * @param locale locale to set
     *
     * @see #getLocale()
     * @since 01.00.00
     * @author Ktt Development
     */
    void setLocale(final Locale locale);

    //

    /**
     * Returns the string for the key.
     *
     * @param key key
     * @return string value
     *
     * @see #getString(String, Object...)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final String key);

    /**
     * Returns a formatted string given parameters.
     *
     * @param key key
     * @param params parameters
     * @return formatted string
     *
     * @see #getString(String)
     * @see String#format(String, Object...)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final String key, final Object... params);

    /**
     * Returns the string for the key for a certain locale.
     *
     * @param locale locale
     * @param key key
     * @return string value
     *
     * @see #getString(Locale, String, Object...)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final Locale locale, final String key);

    /**
     * Returns a formatted string given parameters for a certain locale.
     *
     * @param locale locale
     * @param key key
     * @param params parameters
     * @return formatted string
     *
     * @see #getString(Locale, String)
     * @see String#format(String, Object...)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final Locale locale, final String key, final Object... params);

}
