package com.kttdevelopment.webdir.sitegenerator;

public abstract class Main {

    private static LoggerService loggerService;

    public static LoggerService getLoggerService(){ return loggerService; }

    private static LocaleService localeService;

    public static LocaleService getLocaleService(){ return localeService; }

    private static ConfigService configService;

    public static ConfigService getConfigService(){ return configService; }

    public static void main(String[] args){
        loggerService = new LoggerService();
        localeService = new LocaleService("lang/bundle");

    }

}
