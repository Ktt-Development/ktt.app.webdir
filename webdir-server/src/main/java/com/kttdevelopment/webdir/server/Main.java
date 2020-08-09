package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Main {

    private static PageRenderingService pageRenderingService = null;

    public static PageRenderingService getPageRenderingService(){ return pageRenderingService; }

    private static PermissionsService permissions = null;

    public static PermissionsService getPermissions(){
        return permissions;
    }

    private static FileServer server;

    public static FileServer getServer(){ return server; }

    public static void main(String[] args){
        try{
            Vars.Main.setLoggerService(new LoggerService());
            Vars.Main.setLocaleService(new LocaleService(Vars.Main.localeResource));
            Vars.Main.setConfigService(new ConfigService(Vars.Main.configFile,Vars.Main.configResource));

            final ConfigurationSection config = Vars.Main.getConfigService().getConfig();

            Vars.Main.setPluginLoader(new PluginLoader());
            final File defaults = new File(config.getString(Vars.Config.defaultsKey,Vars.Config.defaultsDir));
            final File source   = new File(config.getString(Vars.Config.sourcesKey,Vars.Config.defaultSource));
            final File output   = new File(config.getString(Vars.Config.outputKey,Vars.Config.defaultOutput));
            pageRenderingService = new PageRenderingService(defaults,source,output);

            final String permissionsKey = config.getString(ServerVars.Config.permissionsKey);
            permissions = new PermissionsService(permissionsKey != null ? new File(permissionsKey) : ServerVars.Main.permissionsFile,ServerVars.Main.permissionsFileResource);
            server = new FileServer(!Vars.Test.server ? config.getInteger(Vars.Config.portKey,Vars.Config.defaultPort) : Vars.Test.getTestPort(),defaults,source,output);
        }catch(final Throwable e){
            try{
                Exceptions.runIgnoreException(() -> Vars.Main.getLoggerService().getLogger("Crash").severe('\n' + Exceptions.getStackTraceAsString(e)));
                Files.write(new File("crash-" + System.currentTimeMillis() + ".txt").toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(final IOException e2){
                e2.printStackTrace();
            }
        }
    }

}
