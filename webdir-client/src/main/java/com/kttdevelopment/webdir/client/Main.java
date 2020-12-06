package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.permissions.Permissions;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.FileUtility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Main {

    static LoggerService logger = null;

        public static LoggerService getLogger(){
        return logger;
    }

        public static Logger getLogger(final String loggerName){
        return logger.getLogger(loggerName);
    }

    static ConfigService config = null;

        public static Map<String,Object> getConfig(){
            return config.getConfiguration();
        }

    static LocaleService locale = null;

        public static LocaleService getLocale(){
        return locale;
    }

    static PluginLoader pluginLoader = null;

        public static PluginLoader getPluginLoader(){
            return pluginLoader;
        }

    static PageRenderingService pageRenderingService = null;

        public static PageRenderingService getPageRenderingService(){
            return pageRenderingService;
        }

    static PermissionsService permissions = null;

        public static Permissions getPermissions(){
            return permissions.getPermissions();
        }

    static FileServer server = null;

        public static FileServer getServer(){
            return server;
        }

    public static void main(String[] args){
        try{
            logger = new LoggerService();
            config = new ConfigService(new File("config.yml"));
            locale = new LocaleService("lang/locale");

            pluginLoader = new PluginLoader(new File(getConfig().get(ConfigService.PLUGINS).toString()));
            pageRenderingService = new PageRenderingService(
                new File(getConfig().get(ConfigService.DEFAULT).toString()),
                new File(getConfig().get(ConfigService.SOURCES).toString()),
                new File(getConfig().get(ConfigService.OUTPUT).toString())
            );

            // server only
            if(Boolean.parseBoolean(getConfig().get(ConfigService.SERVER).toString())){
                permissions = new PermissionsService(new File("permissions.yml"));
                server = new FileServer(getConfig().get(ConfigService.PORT).toString());

            }
        }catch(final Throwable e){
            final long time = System.currentTimeMillis();
            final String response = "---- WebDir Crash Log ----\n" +
                                    "Time: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz").format(time) + '\n' +
                                    "OS: " + System.getProperty("os.name").toLowerCase() + '\n' +
                                    "Logger: "      + logger + '\n' +
                                    "Config: "      + config + '\n' +
                                    "Locale: "      + locale + '\n' +
                                    "Plugins: "     + pluginLoader + '\n' +
                                    "Renderer: "    + pageRenderingService + '\n' +
                                    "Permissions: " + permissions + '\n' +
                                    "Server: "      + server + '\n' +
                                    "---- [ Stack Trace ] ----\n" +
                                    ExceptionUtility.getStackTraceAsString(e);
            try{
                logger.getLogger("WebDir").severe('\n' + response);
            }catch(final Throwable ignored){
                Logger.getGlobal().severe('\n' + response);
            }
            ExceptionUtility.runIgnoreException(
                () -> Files.write(FileUtility.getFreeFile(new File("crash-log-" + time + ".log")).toPath(), response.getBytes(StandardCharsets.UTF_8))
            );
        }
    }

}
