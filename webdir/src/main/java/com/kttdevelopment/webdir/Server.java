package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.IOException;
import java.net.BindException;

import static com.kttdevelopment.webdir.Application.config;
import static com.kttdevelopment.webdir.Application.locale;
import static com.kttdevelopment.webdir.Logger.logger;

public final class Server {

    private final SimpleHttpServer server;

    public final SimpleHttpServer getServer(){
        return server;
    }

    //

    Server(){
        final String prefix = '[' + locale.getString("server") + ']' + ' ';
        logger.info(prefix + locale.getString("server.init.start"));

        // port bind
        final String port = config.get("port").toString();
        try{
            server = SimpleHttpServer.create();

            server.bind(Integer.parseInt(port));
        }catch(final IllegalArgumentException e){
            logger.severe(prefix + locale.getString("server.init.badPort",port));
            throw new RuntimeException(e);
        }catch(final BindException e){
            logger.severe(prefix + locale.getString("server.init.portTaken",port));
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe(prefix + locale.getString("server.init.failed") + '\n' + Logger.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        // init



        // start

        logger.info(prefix + locale.getString("server.init.finished"));
    }

}
