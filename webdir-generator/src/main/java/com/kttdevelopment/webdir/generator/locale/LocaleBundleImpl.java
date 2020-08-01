package com.kttdevelopment.webdir.generator.locale;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.function.*;

import java.util.*;

public final class LocaleBundleImpl implements LocaleBundle {

    private ResourceBundle bundle;
    private final String resource;

    public LocaleBundleImpl(final String resource){
        this.resource = resource;
        setLocale(Locale.getDefault());
    }

    public LocaleBundleImpl(final ILocaleService localeService, final String resource){
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
        return Exceptions.requireNonExceptionElse(() -> bundle.getString(key), null);
    }

    @Override
    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        return Exceptions.requireNonExceptionElse(() -> String.format(Objects.requireNonNull(value),args), value);
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof LocaleBundle))
            return false;
        final LocaleBundle other = (LocaleBundle) o;
        return other.toString().equals(toString());
    }

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
