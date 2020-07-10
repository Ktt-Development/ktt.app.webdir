package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.server.HTMLNameAdapter;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public final class Server {

    Server(final int port, final File rendered) throws IOException{
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("server"));
        final SimpleHttpServer server;
        try{
            server = SimpleHttpServer.create(port);
        }catch(final BindException e){
            logger.severe(locale.getString("server.const.blockedPort",port));
            throw e;
        }catch(final IllegalArgumentException e){
            logger.severe(locale.getString("server.const.invalidPort",port));
            throw e;
        }catch(final IOException e){
            logger.severe(locale.getString("server.const.IO") + '\n' + Exceptions.getStackTraceAsString(e));
            throw e;
        }

        final FileHandler handler = new FileHandler(new HTMLNameAdapter());
        handler.addDirectory(rendered,true);

        server.createContext("",handler);

        server.start();
    }

}
