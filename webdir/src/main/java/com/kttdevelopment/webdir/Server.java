package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.server.DefaultFileHandler;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.config;
import static com.kttdevelopment.webdir.Application.locale;
import static com.kttdevelopment.webdir.LoggerService.logger;

public final class Server {

    private static final Logger logger = Logger.getLogger("WebDir / Server");

    private final SimpleHttpServer server;

    public final SimpleHttpServer getServer(){
        return server;
    }

    //

    Server(){
        final String prefix = '[' + locale.getString("server") + ']' + ' ';
        logger.info(prefix + locale.getString("server.init.start"));

        // port bind
        final int port = config.getConfig().getInteger("key");
        try{
            server = SimpleHttpServer.create();
            server.bind(port);
        }catch(final IllegalArgumentException e){
            logger.severe(prefix + locale.getString("server.init.badPort",port));
            throw new RuntimeException(e);
        }catch(final BindException e){
            logger.severe(prefix + locale.getString("server.init.portTaken",port));
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe(prefix + locale.getString("server.init.failed") + '\n' + LoggerService.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        // init

        final FileHandler fileHandler = new DefaultFileHandler();
        for(final File file : File.listRoots())
            fileHandler.addDirectory(file);

        server.createContext(config.getConfig().getString("head"),fileHandler);

        // start

        logger.info(prefix + locale.getString("server.init.finished"));
    }

}
