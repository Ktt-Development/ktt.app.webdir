package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginShutdownThread;

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
            localeService = new LocaleService("lang/bundle");
            configService = new ConfigService(new File("config.yml"), "/config.yml");

            final ConfigurationSection config = configService.getConfig();

            pluginLoader = new PluginLoader();
            pageRenderingService = new PageRenderingService(new File(config.getString("source_dir",".root")),new File(config.getString("output_dir","_site")));

            if(config.getBoolean("preview"))
                server = new Server(config.getInteger("port",80),new File("_site"));

            Runtime.getRuntime().addShutdownHook(new PluginShutdownThread());
        }catch(final Exception e){
            try{
                e.printStackTrace();
                Files.write(new File("crash-" + System.currentTimeMillis() + ".txt").toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(IOException e2){
                e2.printStackTrace();
            }
        }
    }

}
