package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;

import java.util.*;
import java.util.logging.Logger;

public final class LocaleService {

    private final String resource;

    private final LocaleBundleImpl locale;
    private Locale currentLocale;

    //

    private final List<LocaleBundle> watching = Collections.synchronizedList(new ArrayList<>());

    public synchronized final void setLocale(final Locale locale){
        (Main.getLoggerService() != null ? Main.getLoggerService().getLogger("locale") : Logger.getLogger("Locale")).fine(getString("locale.debug.setLocale",locale.getDisplayName(Locale.getDefault()),locale.getDisplayName(locale)));

        this.locale.setLocale(locale);
        currentLocale = locale;
        watching.forEach(localeBundle -> ((LocaleBundleImpl) localeBundle).setLocale(locale));
    }

    public synchronized final void addWatchedLocale(final LocaleBundle localeBundle){
        (Main.getLoggerService() != null ? Main.getLoggerService().getLogger("locale") : Logger.getLogger("Locale")).finer(getString("locale.debug.addWatchedLocale",localeBundle));

        watching.add(localeBundle);
        ((LocaleBundleImpl) localeBundle).setLocale(currentLocale);
    }

    //

    public final String getString(final String key){
        Logger logger;

        try{ // if key is locale then logger must return a string and not a localized name; required to prevent infinite loop:
             // getString(String) → #getLogger(getString("locale")) ⇆ #getLogger(getString("locale")) ↻
            logger = Main.getLoggerService() != null ? Main.getLoggerService().getLogger(key.equals("locale") ? "Locale" : Objects.requireNonNull(locale.getString("locale"))) : Logger.getLogger("Locale");
        }catch(final NullPointerException ignored){
            logger = Main.getLoggerService().getLogger("Locale");
        }

        final String value = locale.getString(key);
        if(value == null)
            logger.warning(
                String.format(Objects.requireNonNullElse(locale.getString("locale.getString.notFound"),"Failed to get localized string for key '%s' (not found)"),key)
            );
        return value;
    }

    public final String getString(final String key, final Object... args){
        final Logger logger = Main.getLoggerService() != null ? Main.getLoggerService().getLogger(getString("locale")) : Logger.getLogger("Locale");

        final String value = locale.getString(key,args);
        if(value == null)
            logger.warning(
                String.format(Objects.requireNonNullElse(locale.getString("locale.getString.notFound"),"Failed to get localized string for key '%s' (not found)"),key)
            );
        else if(value.equals(getString(key)))
            logger.warning(
                String.format(Objects.requireNonNullElse(locale.getString("locale.getString.missingParams"),"Failed to get localized string for key '%s' (insufficient parameters)"),key)
            );
        return value;
    }

    //

    public LocaleService(String resource_prefix){
        this.resource = resource_prefix;
        final Logger logger = Main.getLoggerService() != null ? Main.getLoggerService().getLogger("Locale") : Logger.getLogger("Locale");
        logger.info("Started locale initialization");

        Locale.setDefault(Locale.US);
        logger.fine("Set current locale to " + Locale.getDefault().getDisplayName(Locale.getDefault()));
        locale = new LocaleBundleImpl(resource_prefix);

        logger.info("Finished locale initialization");
    }

    //


    @Override
    public String toString(){
        return new toStringBuilder("LocaleService")
            .addObject("resource",resource)
            .addObject("localeBundle",locale)
            .addObject("currentLocale (English)",currentLocale.getDisplayName(Locale.US))
            .addObject("currentLocale",currentLocale.getDisplayName(currentLocale))
            .addObject("watching",watching)
            .toString();
    }

}
