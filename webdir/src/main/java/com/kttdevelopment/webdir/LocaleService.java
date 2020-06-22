package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.util.*;
import java.util.logging.Logger;

public final class LocaleService {

    private final LocaleBundle localeBundle = new LocaleBundleImpl();

    public final LocaleBundle getLocale(){
        return localeBundle;
    }

    //

    public final String getString(final String key){
        final Logger logger = Logger.getLogger(Objects.requireNonNullElse(localeBundle.getString("locale"),"Locale"));
        try{
            return Objects.requireNonNull(localeBundle.getString(key));
        }catch(final ClassCastException | NullPointerException | MissingResourceException ignored){
            logger.warning(getString("locale.getString.notFound"));
            try{
                return localeBundle.getString(Locale.ENGLISH, key); // use english as fallback value
            }catch(final ClassCastException | NullPointerException | MissingResourceException ignored2){
                return null;
            }
        }
    }

    public final String getString(final String key, final Object... param){
        final Logger logger = Logger.getLogger(Objects.requireNonNullElse(localeBundle.getString("locale"),"Locale"));
        final String value = getString(key);

        try{
            return String.format(Objects.requireNonNull(value), param);
        }catch(final NullPointerException ignored){
            // logger handled in above
        }catch(final IllegalFormatException ignored){
            logger.warning( getString("locale.getString.param"));
        }
        return value;
    }

    public synchronized final void setLocale(final String locale){
        setLocale(new Locale(locale));
    }

    @SuppressWarnings("SpellCheckingInspection")
    public synchronized final void setLocale(final Locale locale){
        final Logger logger = Logger.getLogger(getString("locale"));

        final String iname = localeBundle.getLocale().getDisplayName();
        final String fname = locale.getDisplayName();

        logger.info(getString("locale.setLocale.initial",iname, fname));

        if(localeBundle.hasLocale(locale)){
            setLocale(locale);
            logger.info(getString("locale.setLocale.finished",iname,fname));
        }else{
            logger.warning(getString("locale.setLocale.notFound",fname));
        }
    }

    //
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] localeCodes = {"en"};
    @SuppressWarnings("SameParameterValue")
    LocaleService(final String resource){
        Logger logger = Logger.getLogger("Locale");
        logger.info("Started locale initialization");

        for(final String code : localeCodes){
            try{
                final Locale locale = new Locale(code);
                localeBundle.addLocale(ResourceBundle.getBundle(
                    resource,
                    locale,
                    LocaleService.class.getClassLoader(),
                    ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
                ));
                logger.finest('+' + code);
            }catch(final NullPointerException | MissingResourceException | IllegalArgumentException e){
                if(code.equalsIgnoreCase("en")){
                    logger.severe("Failed to load default locale");
                    throw e;
                }
            }
        }

        logger.info("Finished locale initialization");
    }

}
