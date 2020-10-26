package com.kttdevelopment.webdir.api;


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
     * Returns the string formatted localized value for a key or just the localized value if there are insufficient parameters. Same as {@link String#format(String, Object...)}.
     *
     * @param key key
     * @param args arguments
     * @return string formatted localized string or unformatted localized string if insufficient parameters
     *
     * @see #getString(String)
     * @see String#format(String, Object...)
     * @since 1.0.0
     * @author Ktt Development
     */
    @SuppressWarnings("SpellCheckingInspection")
    String getString(final String key, final Object... args);

}
