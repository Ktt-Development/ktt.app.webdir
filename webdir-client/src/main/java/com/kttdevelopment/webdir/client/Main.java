package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public abstract class Main {

    public static final File directory = new File("").getAbsoluteFile(); // '../' for exe

    //

    private static LoggerService loggerService;

    public static LoggerService getLoggerService(){ return loggerService; }

    private static void setLoggerService(final LoggerService loggerService){ Main.loggerService = loggerService; }

    private static ConfigService configService;

    public static ConfigService getConfigService(){ return configService; }

    private static void setConfigService(final ConfigService configService){ Main.configService = configService; }

    private static LocaleService localeService;

    public static LocaleService getLocaleService(){ return localeService; }

    private static void setLocaleService(final LocaleService localeService){ Main.localeService = localeService; }
    
    private static PluginLoader pluginLoader;

    public static PluginLoader getPluginLoader(){
        return pluginLoader;
    }

    private static void setPluginLoader(final PluginLoader pluginLoader){
        Main.pluginLoader = pluginLoader;
    }

    private static PageRenderingService pageRenderingService;

    public static PageRenderingService getPageRenderingService(){
        return pageRenderingService;
    }

    private static void setPageRenderingService(final PageRenderingService pageRenderingService){
        Main.pageRenderingService = pageRenderingService;
    }

    private static PermissionsService permissionsService;

    public static PermissionsService getPermissionsService(){
        return permissionsService;
    }

    private static void setPermissionsService(final PermissionsService permissionsService){
        Main.permissionsService = permissionsService;
    }

    //

    public static void main(String[] args){
        setLoggerService(new LoggerService());
        setConfigService(new ConfigService(new File(directory,"config.yml")));
        setLocaleService(new LocaleService("lang/bundle"));
        
        final ConfigurationSection config = getConfigService().getConfig();
        
        final File pluginFolder = new File(directory,config.getString("plugins_dir"));
        setPluginLoader(new PluginLoader(pluginFolder));

        final File defaults = new File(directory,config.getString("default_dir"));
        final File sources  = new File(directory,config.getString("sources_dir"));
        final File output   = new File(directory,config.getString("output_dir"));
        setPageRenderingService(new PageRenderingService(defaults,sources,output));
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
