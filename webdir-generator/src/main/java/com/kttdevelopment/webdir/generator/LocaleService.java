package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;

import java.util.*;
import java.util.logging.Logger;

public final class LocaleService {

    private final LocaleBundleImpl locale;

    //

    public synchronized final void setLocale(final Locale locale){
        this.locale.setLocale(locale);
        // todo: set locale for all plugin services
    }

    //

    public final String getString(final String key){
        final Logger logger = Logger.getLogger(Exceptions.requireNonExceptionElse(() -> getString("locale"), "Locale"));

        final String value = locale.getString(key);
        if(value == null)
            logger.warning(Exceptions.requireNonExceptionElse(
                () -> getString("locale.getString.notFound"),
                String.format("Failed to get localized string for key %s (not found)", key))
            );
        return value;
    }

    public final String getString(final String key, final Object... args){
        final Logger logger = Logger.getLogger(Exceptions.requireNonExceptionElse(() -> getString("locale"), "Locale"));

        final String value = locale.getString(key,args);
        if(value.equals(getString(key)))
            logger.warning(Exceptions.requireNonExceptionElse(
                    () -> getString("locale.getString.missingParams"),
                    String.format("Failed to get localized string for key %s (insufficient parameters)", key))
                );
        return value;
    }

    public LocaleService(String resource_prefix){
        final Logger logger = Logger.getLogger("Locale");
        logger.info("Started locale initialization");

        Locale.setDefault(Locale.US);
        locale = new LocaleBundleImpl(resource_prefix);

        logger.info("Finished locale initialization");
    }

}
