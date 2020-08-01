package com.kttdevelopment.webdir.generator.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.util.Locale;

public interface ILocaleService {

    void setLocale(final Locale locale);

    void addWatchedLocale(final LocaleBundle localeBundle);

    String getString(final String key);

    String getString(final String key, Object... args);

}
