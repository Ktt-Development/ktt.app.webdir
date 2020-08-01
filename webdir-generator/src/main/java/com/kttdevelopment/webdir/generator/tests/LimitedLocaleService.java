package com.kttdevelopment.webdir.generator.tests;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;

import java.util.Locale;

public class LimitedLocaleService implements ILocaleService {

    @Override
    public synchronized final void setLocale(final Locale locale){

    }

    @Override
    public synchronized final void addWatchedLocale(final LocaleBundle localeBundle){

    }

    @Override
    public final String getString(final String key){
        return key;
    }

    @Override
    public final String getString(final String key, final Object... args){
        return key;
    }

}
