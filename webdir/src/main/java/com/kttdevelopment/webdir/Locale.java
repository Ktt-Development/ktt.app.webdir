package com.kttdevelopment.webdir;

import java.util.*;

import static com.kttdevelopment.webdir.Logger.*;

public abstract class Locale {

    private static final String resource = "lang/bundle";

    private static java.util.Locale loadedLocale;
    private static ResourceBundle loadedBundle;

    private static final Map<java.util.Locale,ResourceBundle> locales = new HashMap<>();

    public static java.util.Locale getLocale(){
        return loadedLocale;
    }

    public static Map<java.util.Locale,ResourceBundle> getLocales(){
        return Collections.unmodifiableMap(locales);
    }

    private static final String[] localeCodes = {"en"};

    public static String getString(final String key){
        try{
            return loadedBundle.getString(key);
        }catch(final NullPointerException | MissingResourceException ignored){
            if(getLocale() == null)
                logger.warning("[Locale] Failed to get value for " + key + " (not found)");
            else
                logger.warning('[' + getString("locale") + ']' + ' ' + getString("locale.getString.notFound"));
            return null;
        }
    }

    public static String getString(final String key, final Object... param){
        final String value = getString(key);

        try{
            return String.format(Objects.requireNonNull(value), param);
        }catch(final NullPointerException ignored){
            // logger handled in above
        }catch(final IllegalFormatException ignored){
            if(getLocale() == null)
                logger.warning("[Locale] Failed to format " + key + " (not enough parameters)");
            else
                logger.warning('[' + getString("locale") + ']' + ' ' + getString("locale.getString.param"));
        }
        return value;
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized static boolean setLocale(final String locale){
        return setLocale(new java.util.Locale(locale));
    }

    public synchronized static boolean setLocale(final java.util.Locale locale){
        final ResourceBundle bundle = locales.get(locale);
        final java.util.Locale initLocale = getLocale();

        logger.info(
            '[' + getString("locale") + ']' + ' ' +
            getString(
                "locale.setLocale.initial",
                initLocale == null ? '␀' : initLocale.getDisplayName(),
                locale.getDisplayName()
            )
        );

        if(bundle == null){
            return false;
        }else{
            loadedLocale = locale;
            loadedBundle = bundle;
            logger.info(
                '[' + getString("locale") + ']' + ' ' +
                getString(
                    "locale.setLocale.finished",
                    initLocale == null ? '␀' : initLocale.getDisplayName(),
                    locale.getDisplayName()
                )
            );
            return true;
        }
    }

    private static boolean init = false;
    public synchronized static void main(){
        if(init) return; else init = true;

        logger.info("[Locale] Started locale initialization");

        for(final String code : localeCodes){
            final java.util.Locale locale = new java.util.Locale(code);
            locales.put(
                locale,
                ResourceBundle.getBundle(
                    resource,
                    locale,
                    Locale.class.getClassLoader(),
                    ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
                )
            );
            logger.finest("[Locale] +" + code);

            setLocale(Config.get("locale").toString());

            logger.info('[' + getString("locale") + ']' + ' ' + getString("locale.init.finished"));
        }
    }

}
