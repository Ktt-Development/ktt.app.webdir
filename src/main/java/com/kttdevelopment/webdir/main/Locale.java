package com.kttdevelopment.webdir.main;

import java.util.*;

import static com.kttdevelopment.webdir.main.Logger.*;

public abstract class Locale {

    private static String resourceFolder = "/lang";
    private static String bundleName = "bundle";

    private static java.util.Locale loadedLocale;
    private static ResourceBundle   loadedBundle;
    private static Map<java.util.Locale,ResourceBundle> locales = new HashMap<>();

    public static String getString(final String key){
        return loadedBundle.getString(key);
    }

    public static boolean setLocale(final java.util.Locale locale){
        final ResourceBundle bundle = locales.get(locale);

        if(Objects.isNull(bundle)){
            logger.severe(getString("locale.localeNotFound"));
            return false;
        }else{
            logger.info(String.format(getString("locale.changedLocale"),loadedLocale.getLanguage() + '-' + loadedLocale.getCountry(),locale.getLanguage() + '-' + locale.getCountry()));
            loadedLocale = locale;
            loadedBundle = bundle;
            return true;
        }
    }

    public static boolean setConfigLocale(){
        String code = null;
        try{
            code = Objects.requireNonNull(Config.get("locale")).toString();
            return setLocale(new java.util.Locale(code.substring(0, 2), code.substring(3)));
        }catch(final IndexOutOfBoundsException ignored){
            logger.severe(String.format(Locale.getString("config.invalidKeyValue"),code,"locale"));
        }catch(final NullPointerException ignored){ }
        return false;
    }

    abstract static class Main {

        // do not use locale here (locale gets loaded here)
        synchronized static void init(){
            logger.fine("Started locale init.");
            // load locales here (keep in order of files)
            final String[][] langCode = { {"en","US"} };

            for(final String[] code : langCode){
                final java.util.Locale locale = new java.util.Locale(code[0],code[1]);
                locales.put(locale, ResourceBundle.getBundle( resourceFolder.substring(1) + '/' + bundleName, locale,Main.class.getClassLoader(),ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)));
                logger.finest("Added locale: " + code[0] + '_' + code[1]);
            }

            loadedLocale = new java.util.Locale("en","US");
            loadedBundle = locales.get(loadedLocale);
            logger.fine("Finished locale init.");
        }

    }

}
