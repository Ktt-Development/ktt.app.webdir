package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Main {

    private static LoggerService loggerService = null;

    public static LoggerService getLoggerService(){ return loggerService; }

    private static LocaleService localeService = null;

    public static LocaleService getLocaleService(){ return localeService; }

    private static ConfigService configService = null;

    public static ConfigService getConfigService(){ return configService; }

    private static PluginLoader pluginLoader = null;

    public static PluginLoader getPluginLoader(){ return pluginLoader; }

    private static PageRenderingService pageRenderingService = null;

    public static PageRenderingService getPageRenderingService(){ return pageRenderingService; }

    //

    private static PermissionsService permissions = null;

    public static PermissionsService getPermissions(){
        return permissions;
    }

    private static FileServer server;

    public static FileServer getServer(){ return server; }

    // todo | have application run main from generator and hook into the active server to add handlers [x]
    // todo | ^ this approach can not be used because the handler for static files must be overridden to allow exchange renderers to operate

    public static void main(String[] args){
        try{
            loggerService = new LoggerService();
            localeService = new LocaleService(Vars.Main.localeResource);
            configService = new ConfigService(Vars.Main.configFile,Vars.Main.configResource);

            final ConfigurationSection config = configService.getConfig();

            pluginLoader = new PluginLoader();
            final File defaults = new File(config.getString(Vars.Config.defaultsKey,Vars.Config.defaultsDir));
            final File source = new File(config.getString(Vars.Config.sourcesKey,Vars.Config.defaultSource));
            final File output = new File(config.getString(Vars.Config.outputKey,Vars.Config.defaultOutput));
            pageRenderingService = new PageRenderingService(defaults,source,output);

            permissions = new PermissionsService(new File(config.getString(ServerVars.Config.permissionsKey,ServerVars.Config.defaultPermissions)),ServerVars.Config.defaultPermissions);
            server = new FileServer(config.getInteger(Vars.Config.portKey,Vars.Config.defaultPort),defaults,source,output);

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        }catch(final Throwable e){
            try{
                Exceptions.runIgnoreException(() -> loggerService.getLogger("Crash").severe('\n' + Exceptions.getStackTraceAsString(e)));
                Files.write(new File("crash-" + System.currentTimeMillis() + ".txt").toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(final IOException e2){
                e2.printStackTrace();
            }
        }
    }

}
