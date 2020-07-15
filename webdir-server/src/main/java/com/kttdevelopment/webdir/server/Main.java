package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
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

    private static FileServer server;

    public static FileServer getServer(){ return server; }

    public static void main(String[] args){
        try{
            loggerService = new LoggerService();
            localeService = new LocaleService("lang/bundle");
            configService = new ConfigService(new File("config.yml"), "/config.yml");

            final ConfigurationSection config = configService.getConfig();

            pluginLoader = new PluginLoader();
            final File output = new File(config.getString("output_dir","_site"));
            pageRenderingService = new PageRenderingService(new File(config.getString("source_dir",".root")),new File(config.getString("output_dir","_site")));

            if(config.getBoolean("preview",false))
                server = new FileServer(config.getInteger("port",80),output);

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        }catch(final Exception e){
            try{
                Files.write(new File("crash-" + System.currentTimeMillis()).toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(IOException ignored){ }
        }
    }

}
