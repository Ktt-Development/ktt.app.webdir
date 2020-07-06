package com.kttdevelopment.webdir.sitegenerator;

import java.util.*;
import java.util.logging.Logger;

public class LocaleService {

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
            logger.warning(getString("locale.getString.notFound",key));
            return null;
        }
    }

    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        try{
            return String.format(Objects.requireNonNull(value),args);
        }catch(final NullPointerException | IllegalFormatException e){
            if(e instanceof IllegalFormatException)
                logger.warning("locale.getString.missingParams");
        }
        return value;
    }

    LocaleService(String resource_prefix){
        logger = Logger.getLogger("Locale"); // EN
        logger.info("Started locale initialization");

        Locale.setDefault(Locale.US);
        locale = Locale.getDefault();

        final ClassLoader classLoader = LocaleService.class.getClassLoader();
        final ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

        for(final Locale locale : supportedLocales){
            bundles.put(
                locale,
                ResourceBundle.getBundle(
                    resource_prefix,
                    locale,
                    classLoader,
                    control
                )
            );
            logger.fine("Loaded locale: " + locale.toString());
        }
        logger.info("Finished locale initialization");
    }

}
