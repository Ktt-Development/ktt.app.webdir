package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.server.DefaultFileHandler;
import com.kttdevelopment.webdir.server.StaticFileHandler;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

import static com.kttdevelopment.webdir.Application.config;
import static com.kttdevelopment.webdir.Application.locale;

public final class Server {

    private static final Logger logger = Logger.getLogger("WebDir / Server");

    private final SimpleHttpServer server;

    public final SimpleHttpServer getServer(){
        return server;
    }

    //

    Server(){
        logger.info(locale.getString("server.init.start"));

        // port bind
        final int port = config.getConfig().getInteger("key");
        try{
            server = SimpleHttpServer.create();
            server.bind(port);
        }catch(final IllegalArgumentException e){
            logger.severe(locale.getString("server.init.badPort",port));
            throw new RuntimeException(e);
        }catch(final BindException e){
            logger.severe(locale.getString("server.init.portTaken",port));
            throw new RuntimeException(e);
        }catch(final IOException e){
            logger.severe(locale.getString("server.init.failed") + '\n' + LoggerService.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
        // init

        final FileHandler staticHandler = new StaticFileHandler();
        staticHandler.addDirectory(new File(Application.parent + '\\' + config.getConfig().getString("source")));
        server.createContext("",staticHandler);

        //

        final FileHandler fileHandler = new DefaultFileHandler();
        for(final File file : File.listRoots())
            fileHandler.addDirectory(file);

        server.createContext(config.getConfig().getString("files"),fileHandler);

        // start

        logger.info(locale.getString("server.init.finished"));
    }

}
