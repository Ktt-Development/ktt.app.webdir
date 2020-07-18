package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Main {

    private static LoggerService loggerService;

    public static LoggerService getLoggerService(){ return loggerService; }

    private static LocaleService localeService;

    public static LocaleService getLocaleService(){ return localeService; }

    private static ConfigService configService;

    public static ConfigService getConfigService(){ return configService; }

    private static PluginLoader pluginLoader;

    public static PluginLoader getPluginLoader(){ return pluginLoader; }

    private static PageRenderingService pageRenderingService;

    public static PageRenderingService getPageRenderingService(){ return pageRenderingService; }

    //

    private static Server server;

    public static Server getServer(){ return server; }

    public static void main(String[] args){
        try{
            loggerService = new LoggerService();
            localeService = new LocaleService(Vars.Main.localeResource);
            configService = new ConfigService(Vars.Main.configFile,Vars.Main.configResource);

            final ConfigurationSection config = configService.getConfig();

            pluginLoader = new PluginLoader();
            final File source = new File(config.getString(Vars.Config.sourcesKey,Vars.Config.defaultSource));
            final File output = new File(config.getString(Vars.Config.outputKey,Vars.Config.defaultOutput));
            pageRenderingService = new PageRenderingService(source,output);

            if(Vars.Test.server || config.getBoolean(Vars.Config.serverKey,Vars.Config.defaultServer))
                server = new Server(config.getInteger(Vars.Config.portKey,Vars.Config.defaultPort),source,output);

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        }catch(final Throwable e){
            try{
                Exceptions.runIgnoreException(() -> loggerService.getLogger("Crash").severe(Exceptions.getStackTraceAsString(e)));
                Files.write(new File("crash-" + System.currentTimeMillis() + ".txt").toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(final IOException e2){
                e2.printStackTrace();
            }
        }
    }

}
