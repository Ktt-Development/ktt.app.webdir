package com.kttdevelopment.webdir.generator.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.util.*;

public final class LocaleBundleImpl implements LocaleBundle {

    private ResourceBundle bundle;
    private final String resource;

    public LocaleBundleImpl(final String resource){
        this.resource = resource;
        setLocale(Locale.getDefault());
    }

    public LocaleBundleImpl(final LocaleService localeService, final String resource){
        this.resource = resource;
        setLocale(Locale.getDefault());
        localeService.addWatchedLocale(this);
    }

    public synchronized final void setLocale(final Locale locale){
        bundle = ResourceBundle.getBundle(
            resource,
            locale,
            getClass().getClassLoader(),
            new UTF8PropertiesControl()
        );
    }

    //

    @Override
    public final String getString(final String key){
        try{
            return bundle.getString(key);
        }catch(final MissingResourceException | NullPointerException e){
            return null;
        }
    }

    @Override
    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        try{
            return String.format(Objects.requireNonNull(value),args);
        }catch(final NullPointerException | IllegalFormatException e){
            return value;
        }
    }

    //


    @Override
    public String toString(){
        return new toStringBuilder("LocaleBundle")
            .addObject("locale (English)",bundle.getLocale().getDisplayName(Locale.US))
            .addObject("locale",bundle.getLocale().getDisplayName(bundle.getLocale()))
            .addObject("bundle",bundle)
            .addObject("resource",resource)
            .toString();
    }

}
