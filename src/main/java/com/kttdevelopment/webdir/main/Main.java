package com.kttdevelopment.webdir.main;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.RootHandler;

import java.io.IOException;
import java.net.BindException;

import static com.kttdevelopment.webdir.main.Logger.*;

public class Main {

    static final String root = "";

    private static SimpleHttpServer server;

    public static void main(String[] args){
        try{
            Logger.Main.init();
            Locale.Main.init();
            Config.Main.init();
            Locale.setConfigLocale();
            Directory.Main.init();


            try{
                server = SimpleHttpServer.create(Integer.parseInt(Config.get("port").toString()));
            }catch(final NullPointerException | IllegalArgumentException e){
                logger.severe(Locale.getString("server.invalidPort"));
                throw e;
            }catch(final BindException e){
                logger.severe(Locale.getString("server.portTaken"));
                throw e;
            }catch(final IOException e){
                logger.severe(Locale.getString("server.failedCreate." + '\n' + Logger.getStackTraceAsString(e)));
                throw e;
            }

        }catch(final Exception e){
            
        }finally{
            // close handle
        }
    }

}
