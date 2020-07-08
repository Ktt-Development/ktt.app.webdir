package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.ConfigService;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.LoggerService;
import com.kttdevelopment.webdir.generator.Server;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginShutdownThread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

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

            pluginLoader = new PluginLoader();
            pageRenderingService = new PageRenderingService(new File(".root"),new File("_site"));

            // todo: add port
            server = new Server(80, new File("_site"));
            Runtime.getRuntime().addShutdownHook(new PluginShutdownThread());
        }catch(final Exception e){
            try{
                Files.write(new File("/crash-" + System.currentTimeMillis()).toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(IOException ignored){ }
        }
    }

}
