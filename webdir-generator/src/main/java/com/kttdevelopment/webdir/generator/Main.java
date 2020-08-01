package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Main {

    private static PageRenderingService pageRenderingService = null;

    public static PageRenderingService getPageRenderingService(){ return pageRenderingService; }

    //

    private static Server server = null;

    public static Server getServer(){ return server; }

    public static void main(String[] args){
        try{
            Vars.Main.setLoggerService(new LoggerService());
            Vars.Main.setLocaleService(new LocaleService(Vars.Main.localeResource));
            Vars.Main.setConfigService(new ConfigService(Vars.Main.configFile,Vars.Main.configResource));

            final ConfigurationSection config = Vars.Main.getConfigService().getConfig();

            Vars.Main.setPluginLoader(new PluginLoader());
            final File defaults = new File(config.getString(Vars.Config.defaultsKey,Vars.Config.defaultsDir));
            final File source = new File(config.getString(Vars.Config.sourcesKey,Vars.Config.defaultSource));
            final File output = new File(config.getString(Vars.Config.outputKey,Vars.Config.defaultOutput));
            pageRenderingService = new PageRenderingService(defaults,source,output);

            if(Vars.Test.server || config.getBoolean(Vars.Config.serverKey,Vars.Config.defaultServer))
                server = new Server(config.getInteger(Vars.Config.portKey,Vars.Config.defaultPort),source,output);

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        }catch(final Throwable e){
            try{
                Exceptions.runIgnoreException(() -> Vars.Main.getLoggerService().getLogger("Crash").severe('\n' + Exceptions.getStackTraceAsString(e)));
                Files.write(new File("crash-" + System.currentTimeMillis() + ".txt").toPath(), Exceptions.getStackTraceAsString(e).getBytes());
            }catch(final IOException e2){
                e2.printStackTrace();
            }
        }
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("Main")
            .addObject("loggerService",Vars.Main.getLoggerService())
            .addObject("localeService",Vars.Main.getLocaleService())
            .addObject("configService",Vars.Main.getConfigService())
            .addObject("pluginLoader",Vars.Main.getPluginLoader())
            .addObject("pageRenderingService",pageRenderingService)
            .addObject("server",server)
            .toString();
    }

}
