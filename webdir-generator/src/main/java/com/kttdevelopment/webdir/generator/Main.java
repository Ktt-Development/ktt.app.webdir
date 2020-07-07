package com.kttdevelopment.webdir.generator;

public abstract class Main {

    private static LoggerService loggerService;

    public static LoggerService getLoggerService(){ return loggerService; }

    private static LocaleService localeService;

    public static LocaleService getLocaleService(){ return localeService; }

    private static ConfigService configService;

    public static ConfigService getConfigService(){ return configService; }

    private static PluginLoader pluginLoader;

    public static PluginLoader getPluginLoader(){ return pluginLoader; }

    public static void main(String[] args){
        try{
            loggerService = new LoggerService();
            localeService = new LocaleService("lang/bundle");
            configService = new ConfigService(null, null);

            pluginLoader = new PluginLoader();
            // file generator +walk handle
            // server only if serve
        }catch(final Exception e){
            // handle close
        }
    }

}
