package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;
import com.kttdevelopment.webdir.client.logger.QueuedLoggerMessage;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LocaleService {

    private final String resource;

    private final LocaleBundleImpl locale;
    private Locale currentLocale;

    // bundles tracking current locale

    private final List<LocaleBundle> watching = new ArrayList<>();

    public synchronized final void setLocale(final Locale locale){
        Main.getLoggerService().getLogger(getString("localeService")).info(getString("localeService.setLocale.changed",currentLocale.getDisplayName(locale),currentLocale.getDisplayName(Locale.US),locale.getDisplayName(locale),locale.getDisplayName(Locale.US)));
        this.locale.setLocale(locale);
        currentLocale = locale;
        watching.forEach(bundle -> ((LocaleBundleImpl) bundle).setLocale(locale));
    }

    // this method guarantees that the watching list is thread safe
    public synchronized final void addWatchedLocale(final LocaleBundle localeBundle){
        Main.getLoggerService().getLogger(getString("localeService")).info(getString("localeService.addWatchedLocale.added",localeBundle));
        watching.add(localeBundle);
        ((LocaleBundleImpl) localeBundle).setLocale(currentLocale);
    }

    //

    public final String getString(final String key){
        return getString(key, (Object) null);
    }

    public final String getString(final String key, final Object... args){
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("localeService","Locale Service"));

        final boolean noArgs = args == null || args.length == 0;

        final String value = noArgs ? locale.getString(key) : locale.getString(key,args);
        if(value == null)
            logger.warning(
                String.format(Objects.requireNonNullElse(locale.getString("localeService.getString.notFound"),"Failed to get localized string for key '%s' (not found)"),key)
            );
        else if(!noArgs && value.equals(locale.getString(key)))
            logger.warning(
                String.format(Objects.requireNonNullElse(locale.getString("localeService.getString.missingParams"),"Failed to get localized string for key '%s' (insufficient parameters)"),key)
            );
        return value;
    }

    //

    LocaleService(final String resource_prefix){
        final LoggerService loggerService   = Main.getLoggerService();
        final Logger logger                 = loggerService.getLogger("Locale Service");

        loggerService.addQueuedLoggerMessage(
            "localeService", "localeService.const.started",
            logger.getName(), "Started locale service initialization",
            Level.INFO
        );

        Objects.requireNonNull(resource_prefix);
        this.resource = resource_prefix;

        Locale.setDefault(Locale.US);
        locale = new LocaleBundleImpl(resource_prefix);
        final Locale configLocale = new Locale(Main.getConfigService().getConfig().getString("locale"));
        locale.setLocale(configLocale);
        currentLocale = configLocale;

        loggerService.addQueuedLoggerMessage(
            "localeService", "localeService.const.setLocale",
            logger.getName(), "Set current locale to %s",
            Level.FINE,currentLocale.getDisplayName()
        );

        logger.info(getString("localeService.const.printQueuedMessages"));
        // send queued localized messages
        for(final QueuedLoggerMessage message : Main.getLoggerService().getQueuedLoggerMessages())
            loggerService.getLogger(getString(message.getLocalizedLogger())).log(message.getLevel(),getString(message.getKey(),message.getArgs()));

        logger.info(getString("localeService.const.finished"));
    }

    //


    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("resource",resource)
            .addObject("localeBundle",locale)
            .addObject("currentLocale (English)",currentLocale.getDisplayName(Locale.US))
            .addObject("currentLocale",currentLocale.getDisplayName(currentLocale))
            .addObject("watching", watching)
            .toString();
    }

}
