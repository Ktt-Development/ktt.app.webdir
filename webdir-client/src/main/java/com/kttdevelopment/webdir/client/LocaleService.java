package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;
import com.kttdevelopment.webdir.client.logger.QueuedLoggerMessage;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LocaleService {

    private final String resource;

    private final LocaleBundleImpl locale;
    private Locale currentLocale = Locale.getDefault();

    private final List<LocaleBundleImpl> watching = new ArrayList<>();

    LocaleService(final String resource){
        final LoggerService loggerService = Main.getLogger();

        loggerService.addQueuedLoggerMessage(
            "locale.name", "locale.constructor.start",
            "Locale Service", "Started locale service initialization.",
            Level.FINE
        );

        this.resource = Objects.requireNonNull(resource);

        locale = new LocaleBundleImpl(resource);
        addWatchedLocale(locale);

        final Locale configLocale = new Locale(Main.getConfig().string(ConfigService.LANG));
        setLocale(configLocale);

        final Logger logger = Main.getLogger(getString("locale.name"));
        logger.fine(getString("locale.constructor.set", currentLocale.getDisplayName()));

        // send queued messages
        logger.info(getString("locale.constructor.queue") + '\n' + "---- [ Queued Messages ] ---");
        if(!currentLocale.equals(Locale.US))
            for(final QueuedLoggerMessage qlm : Main.getLogger().getQueuedLoggerMessages())
                loggerService.getLogger(getString(qlm.getLocalizedLoggerKey())).log(qlm.getLevel(), getString(qlm.getLocaleKey(), qlm.getArgs()));
        Main.getLogger().getQueuedLoggerMessages().clear(); // clear so subsequent locales don't repeat messages
        logger.info(getString("locale.constructor.finish"));
    }

    public synchronized final void setLocale(final Locale locale){
        final Locale init = currentLocale;
        currentLocale = locale;
        for(final LocaleBundleImpl bundle : watching)
            bundle.setLocale(locale);
        Main.getLogger(getString("locale.name")).info(
            getString("locale.locale.changed",
                  init.getDisplayName(),
                     currentLocale.getDisplayName(currentLocale)));
    }

    public final Locale getLocale(){
        return currentLocale;
    }

    public synchronized final void addWatchedLocale(final LocaleBundleImpl locale){
        watching.add(locale);
        locale.setLocale(currentLocale);
        Main.getLogger(getString("locale.name")).fine(getString("locale.locale.watched", locale));
    }

    public final String getString(final String key){
        return getString(key,(Object[]) null);
    }

    // this method runs under the assumption that locale.name, null, and args have values
    public final String getString(final String key, final Object... args){
        final boolean noArgs = args == null || args.length == 0;
        final String value = noArgs ? locale.getString(key) : locale.getString(key, args);
        if(value == null)
            Main.getLogger(getString("locale.name")).warning(getString("locale.locale.null", key));
        else if(!noArgs && value.equals(locale.getString(key)))
            Main.getLogger(getString("locale.name")).warning(getString("locale.locale.args", key));
        return value;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("resource", resource)
            .addObject("locale", locale)
            .addObject("currentLocale", currentLocale)
            .addObject("watching", watching)
            .toString();
    }

}
