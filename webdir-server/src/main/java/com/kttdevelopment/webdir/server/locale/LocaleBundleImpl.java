package com.kttdevelopment.webdir.server.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.util.*;

public class LocaleBundleImpl implements LocaleBundle {

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
    public final synchronized void setLocale(final Locale locale){
        loadedLocale = locale;
        loadedBundle = locales.get(locale);
    }

    //

    @Override
    public final String getString(final String key){
        try{
            return Objects.requireNonNull(loadedBundle.getString(key));
        }catch(final ClassCastException | NullPointerException | MissingResourceException ignored){
            try{
                return Objects.requireNonNull(locales.get(Locale.ENGLISH).getString(key)); // use english as fallback value
            }catch(final NullPointerException ignored2){
                return null;
            }
        }
    }

    @Override
    public final String getString(final String key, final Object... params){
        final String value = getString(key);
        try{
            return String.format(Objects.requireNonNull(value), params);
        }catch(final NullPointerException | IllegalFormatException ignored){
            return value;
        }
    }

    @Override
    public String getString(final Locale locale, final String key){
        return locales.get(locale).getString(key);
    }

    @Override
    public String getString(final Locale locale, final String key, final Object... params){
        return String.format(locales.get(locale).getString(key),params);
    }

}
