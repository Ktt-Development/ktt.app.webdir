package com.kttdevelopment.webdir.main;

import java.util.*;

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
            Logger.logger.severe(getString("locale.localeNotFound"));
            return false;
        }else{
            Logger.logger.severe(String.format(getString("locale.changedLocale"),loadedLocale.getLanguage() + '-' + loadedLocale.getCountry(),locale.getLanguage() + '-' + locale.getCountry()));
            loadedLocale = locale;
            loadedBundle = bundle;
            return true;
        }
    }

    abstract static class Main {

        // do not use locale here (locale gets loaded here)
        synchronized static void init(){
            // load locales here (keep in order of files)
            final String[][] langCode = { {"en","US"} };

            for(final String[] code : langCode){
                final java.util.Locale locale = new java.util.Locale(code[0],code[1]);
                locales.put(locale, ResourceBundle.getBundle( resourceFolder.substring(1) + '/' + bundleName, locale,Main.class.getClassLoader(),ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)));
            }

            loadedLocale = new java.util.Locale("en","US");
            loadedBundle = locales.get(loadedLocale);
        }

    }

}
