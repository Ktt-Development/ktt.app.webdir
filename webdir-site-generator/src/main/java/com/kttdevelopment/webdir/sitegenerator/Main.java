package com.kttdevelopment.webdir.sitegenerator;

public abstract class Main {

    private static LoggerService loggerService;

    public final LoggerService getLoggerService(){ return loggerService; }

    private static LocaleService localeService;

    public final LocaleService getLocaleService(){ return localeService; }

    public static void main(String[] args){
        loggerService = new LoggerService();
        localeService = new LocaleService("bundle");
    }

}
