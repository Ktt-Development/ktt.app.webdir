package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.locale.LocaleBundleImpl;

import java.util.*;

import static com.kttdevelopment.webdir.LoggerService.logger;

public final class LocaleService {

    private final LocaleBundle localeBundle = new LocaleBundleImpl();

    public final LocaleBundle getLocale(){
        return localeBundle;
    }

    //

    public final String getString(final String key){
        try{
            return localeBundle.getString(key);
        }catch(final ClassCastException | NullPointerException | MissingResourceException ignored){
            logger.warning('[' + getString("locale") + ']' + ' ' + getString("locale.getString.notFound"));
            return null;
        }
    }

    public final String getString(final String key, final Object... param){
        final String value = getString(key);

        try{
            return String.format(Objects.requireNonNull(value), param);
        }catch(final NullPointerException ignored){
            // logger handled in above
        }catch(final IllegalFormatException ignored){
            logger.warning('[' + getString("locale") + ']' + ' ' + getString("locale.getString.param"));
        }
        return value;
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final void setLocale(final String locale){
        setLocale(new Locale(locale));
    }

    public synchronized final void setLocale(final Locale locale){
        final LocaleBundle bundle = localeBundle;

        final String prefix = '[' + getString("locale") + ']' + ' ';

        logger.info(
            prefix +
            getString(
                "locale.setLocale.initial",
                bundle.getLocale().getDisplayName(),
                locale.getDisplayName()
            )
        );

        if(localeBundle.hasLocale(locale)){
            setLocale(locale);
            logger.info(
                prefix +
                getString("locale.setLocale.finished",bundle.getLocale().getDisplayName(),locale.getDisplayName())
            );
        }else{
            logger.warning(
                prefix +
                getString("locale.setLocale.notFound",locale.getDisplayName())
            );
        }
    }

    //
    @SuppressWarnings("FieldCanBeLocal")
    private final String[] localeCodes = {"en"};
    LocaleService(final String resource){
        final String prefix = "[Locale]" + ' ';

        logger.info(prefix + "Started locale initialization");

        for(final String code : localeCodes){
            try{
                final Locale locale = new Locale(code);
                localeBundle.addLocale(ResourceBundle.getBundle(
                    resource,
                    locale,
                    LocaleService.class.getClassLoader(),
                    ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
                ));
                logger.finest(prefix + '+' + code);
            }catch(final NullPointerException | MissingResourceException | IllegalArgumentException e){
                if(code.equalsIgnoreCase("en")){
                    logger.severe(prefix + "Failed to load default locale");
                    throw new RuntimeException(e);
                }
            }
        }

        setLocale(Application.config.getConfig().getString("locale","en"));

        logger.info('[' + getString("locale") + ']' + ' ' + getString("locale.init.finished"));
    }

}
