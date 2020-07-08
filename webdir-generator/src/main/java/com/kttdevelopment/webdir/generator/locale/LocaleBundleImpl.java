package com.kttdevelopment.webdir.generator.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;

import java.util.*;

public class LocaleBundleImpl implements LocaleBundle {

    private ResourceBundle bundle;
    private final String resource;

    public LocaleBundleImpl(final String resource){
        this.resource = resource;
        setLocale(Locale.getDefault());
    }

    public synchronized final void setLocale(final Locale locale){
        bundle = ResourceBundle.getBundle(
            resource,
            locale,
            getClass().getClassLoader(),
            ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
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

}
