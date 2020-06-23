package com.kttdevelopment.webdir.pluginservice;

import com.kttdevelopment.webdir.*;
import com.kttdevelopment.webdir.api.WebDirPlugin;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ShutdownThread extends Thread {

    @Override
    public synchronized final void run(){
        final Logger logger = Logger.getLogger("main");
        final LocaleService locale = Application.getLocaleService();

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final List<WebDirPlugin> plugins = Application.getPluginService().getLibrary().getPlugins();

        plugins.forEach((plugin) -> {
            final Future<?> future = executor.submit(() -> {
               plugin.onDisable();
               logger.info(locale.getString("pluginService.shutdown.unloaded",plugin.getPluginService().getPluginName()));
            });

            try{ // this executes the above runnable
                future.get(30,TimeUnit.SECONDS);
            }catch(final Exception e){
                future.cancel(true);
                try{
                    logger.severe(locale.getString(e instanceof TimeoutException ? "pluginService.shutdown.timeout" : "pluginService.shutdown.unknown", plugin.getPluginService().getPluginName()) + (e instanceof TimeoutException ? "" : '\n' + LoggerService.getStackTraceAsString(e)));
                }catch(final Exception ignored){
                    logger.severe(locale.getString("pluginService.shutdown.badName") + '\n' + LoggerService.getStackTraceAsString(e));
                }
            }
        });
        executor.shutdown();
    }

}
