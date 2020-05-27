package com.kttdevelopment.webdir.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.util.*;

public final class LocaleBundleImpl implements LocaleBundle {

    private final Map<Locale,ResourceBundle> locales = new HashMap<>();
    private ResourceBundle loadedBundle;
    private Locale loadedLocale;

    //

    @Override
    public final void addLocale(final ResourceBundle bundle){
        locales.put(bundle.getLocale(),bundle);
    }

    @Override
    public final boolean hasLocale(final Locale locale){
        return locales.containsKey(locale);
    }

    @Override
    public final Locale getLocale(){
        return loadedLocale;
    }

    @Override
    public final void setLocale(final Locale locale){
        loadedLocale = locale;
        loadedBundle = locales.get(locale);
    }

    //

    @Override
    public final String getString(final String key){
        return loadedBundle.getString(key);
    }

    @Override
    public final String getString(final String key, final Object... params){
        return String.format(loadedBundle.getString(key),params);
    }

}
