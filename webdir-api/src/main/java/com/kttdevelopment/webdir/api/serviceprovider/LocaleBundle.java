package com.kttdevelopment.webdir.api.serviceprovider;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LocaleBundle {

    void addLocale(final ResourceBundle bundle);

    boolean hasLocale(final Locale locale);

    Locale getLocale();

    void setLocale(final java.util.Locale locale);

    //

    String getString(final String key);

    String getString(final String key, final Object... params);

    String getString(final Locale locale, final String key);

    String getString(final Locale locale, final String key, final Object... params);

}
