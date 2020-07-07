package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.util.*;
import java.util.logging.Logger;

public final class LocaleService {

    private static final Map<Locale,ResourceBundle> bundles = new HashMap<>();

    private static final Locale[] supportedLocales = {
        new Locale("EN","US")
    };

    private Locale locale;

    private Logger logger;

    //

    public synchronized final void setLocale(final Locale locale){
        this.locale = locale;
        logger = Logger.getLogger(getString("locale"));
    }

    public final String getString(final String key){
        try{
            return bundles.get(locale).getString(key);
        }catch(final MissingResourceException | NullPointerException e){
            logger.warning(Exceptions.requireNonExceptionElse(
                () -> getString("locale.getString.notFound"),
                String.format("Failed to get localized string for key %s (not found)", key))
            );
            return null;
        }
    }

    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        try{
            return String.format(Objects.requireNonNull(value),args);
        }catch(final NullPointerException | IllegalFormatException e){
            if(e instanceof IllegalFormatException)
                logger.warning(Exceptions.requireNonExceptionElse(
                    () -> getString("locale.getString.missingParams"),
                    String.format("Failed to get localized string for key %s (insufficient parameters)", key))
                );
        }
        return value;
    }

    LocaleService(String resource_prefix){
        logger = Logger.getLogger("Locale");
        logger.info("Started locale initialization");

        Locale.setDefault(Locale.US); // default
        locale = Locale.getDefault();

        final ClassLoader classLoader = LocaleService.class.getClassLoader();
        final ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

        for(final Locale locale : supportedLocales){
            try{
                bundles.put(
                        locale,
                        ResourceBundle.getBundle(
                                resource_prefix,
                                locale,
                                classLoader,
                                control
                        )
                );
                logger.fine("Loaded locale: " + locale);
            }catch(final MissingResourceException ignored){
                logger.warning("No locale bundle found for " + locale);
            }
        }
        logger.info("Finished locale initialization");
    }

}
