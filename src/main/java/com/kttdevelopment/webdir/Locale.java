package com.kttdevelopment.webdir;

import java.util.*;

import static com.kttdevelopment.webdir.Logger.*;

public abstract class Locale {

    private static final String resource = "lang/bundle";

    private static java.util.Locale loadedLocale;
    private static ResourceBundle loadedBundle;
    private static final Map<java.util.Locale,ResourceBundle> locales = new HashMap<>();

    public static java.util.Locale getLoadedLocale(){
        return loadedLocale;
    }

    public static Map<java.util.Locale,ResourceBundle> getLocales(){
        return Collections.unmodifiableMap(locales);
    }

    private static final String[] localeCodes = {"en"};

    //

    public static String getString(final String key){
        return loadedBundle.getString(key);
    }

    public static String getString(final String key, final Object... param){
        try{
            return String.format(getString(key),param);
        }catch(final IllegalFormatException e){
            // severe debug
            return getString(key);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized static boolean setLocale(final java.util.Locale locale){
        final ResourceBundle bundle = locales.get(locale);

        if(bundle == null){
            return false;
        }else{
            loadedLocale = locale;
            loadedBundle = bundle;
            return true;
        }
    }

    //

    private static boolean init = false;

    public synchronized static void main(){
        if(init) return; init = true;
        logger.info("Started locale init");

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
            logger.finest("Added locale " + code);
        }

        setLocale(new java.util.Locale("en"));

        logger.info("Finished locale init");
    }



}
