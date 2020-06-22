package com.kttdevelopment.webdir;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class Application {

    public static final String parent = new File("").getAbsolutePath();

    //

    private static LoggerService logger;
    private static LocaleService locale;
    private static ConfigService config;
    private static PermissionsService permissions;
    private static Server server;
    private static PluginServiceLoader pluginService;

    //

    @SuppressWarnings("InstantiationOfUtilityClass")
    public synchronized static void main(String[] args) throws IOException{ try{
        logger = new LoggerService();
        locale = new LocaleService("lang/bundle");
        config = new ConfigService(
            new File(parent + '\\' + "config.yml"),
            "config.yml"
        );
        permissions = new PermissionsService(
            new File(parent + '\\' + "permissions.yml"),
            new File(Application.class.getClassLoader().getResource("permissions.yml").getFile())
        );
        server = new Server();
        pluginService = new PluginServiceLoader(new File(parent + '\\' + "plugins"));

        server.getServer().start();
    }catch(final Exception e){
        Logger.getLogger("main").severe(LoggerService.getStackTraceAsString(e));
        throw e;
    } }

    //


    public static LoggerService getLoggerService(){
        return logger;
    }

    public static LocaleService getLocaleService(){
        return locale;
    }

    public static ConfigService getConfigService(){
        return config;
    }

    public static PermissionsService getPermissionsService(){
        return permissions;
    }

    public static Server getServer(){
        return server;
    }

    public static PluginServiceLoader getPluginService(){
        return pluginService;
    }

}
