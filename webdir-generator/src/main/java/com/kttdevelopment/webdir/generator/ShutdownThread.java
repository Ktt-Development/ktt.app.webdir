package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class ShutdownThread extends Thread{

    @Override
    public final void run(){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger = Vars.Main.getLoggerService().getLogger(locale.getString("shutdown"));

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final AtomicInteger success = new AtomicInteger(0);
        final List<WebDirPlugin> plugins = Vars.Main.getPluginLoader().getPlugins();
        plugins.forEach(plugin -> {
            final Future<?> future = executor.submit(plugin::onDisable);

            try{
                future.get(Vars.Plugin.loadTimeout,Vars.Plugin.loadTimeoutUnit);
                success.incrementAndGet();
            }catch(final Throwable e){
                future.cancel(true);
                logger.severe(
                    e instanceof TimeoutException
                    ? locale.getString("shutdown.run.timedOut",plugin.getPluginYml().getPluginName(),Vars.Plugin.loadTimeout + " " + Vars.Plugin.loadTimeoutUnit.name().toLowerCase())
                    : locale.getString("shutdown.run.unknown",plugin.getPluginYml().getPluginName()) + '\n' + Exceptions.getStackTraceAsString(e)
                );
            }
        });

        logger.info(locale.getString("shutdown.run.disabled",success.get(),plugins.size()));

        executor.shutdown();
    }

}
