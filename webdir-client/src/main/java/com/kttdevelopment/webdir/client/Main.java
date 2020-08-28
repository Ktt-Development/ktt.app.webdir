package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.File;

public abstract class Main {

    public static final File directory = new File(""); // '../' for exe

    //

    private static LoggerService loggerService;

    public static LoggerService getLoggerService(){ return loggerService; }

    public static void setLoggerService(final LoggerService loggerService){ Main.loggerService = loggerService; }

    private static ConfigService configService;

    public static ConfigService getConfigService(){ return configService; }

    public static void setConfigService(final ConfigService configService){ Main.configService = configService; }

    private static LocaleService localeService;

    public static LocaleService getLocaleService(){ return localeService; }

    public static void setLocaleService(final LocaleService localeService){ Main.localeService = localeService; }

    //

    public static void main(String[] args){
        setLoggerService(new LoggerService());
        setConfigService(new ConfigService(new File(directory,"config.yml"),args));
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("directory",directory)
            .addObject("loggerService",loggerService)
            .addObject("configService",configService)
            .addObject("localeService",localeService)
            .toString();
    }

}
