package com.kttdevelopment.webdir.client.locale;

import com.kttdevelopment.webdir.api.LocaleBundle;
import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleBundleImpl implements LocaleBundle {

    private final String resource;

    private final Map<String,String> loaded = new ConcurrentHashMap<>();
    private Locale locale;

    public LocaleBundleImpl(final String resource){
        this(null, resource);
    }

    public LocaleBundleImpl(final LocaleService localeService, final String resource){
        if(localeService != null)
            setLocale(Locale.getDefault());
        this.resource = resource;
        // todo: add watched

        // todo: populate localized from resources


    }

    public synchronized final void setLocale(final Locale locale){
        this.locale = locale;

        loaded.clear();
        // populate in reverse order so specific locales override

        // fixme: change so files are read here
        // loaded.putAll(Objects.requireNonNullElse(localized.get(new Locale("en", "US")), new HashMap<>()));
        // loaded.putAll(Objects.requireNonNullElse(localized.get(new Locale("en")), new HashMap<>()));
        // loaded.putAll(Objects.requireNonNullElse(localized.get(new Locale(locale.getLanguage())), new HashMap<>()));
        // loaded.putAll(Objects.requireNonNullElse(localized.get(locale), new HashMap<>()));
    }

    //

    @Override
    public final String getString(final String key){
        return ExceptionUtility.requireNonExceptionElse(() -> loaded.get(key), null);
    }

    @Override
    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        return ExceptionUtility.requireNonExceptionElse(() -> String.format(Objects.requireNonNull(value),args), value);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("resource", resource)
            .addObject("loaded", loaded)
            .addObject("locale", locale)
            .toString();
    }

}
