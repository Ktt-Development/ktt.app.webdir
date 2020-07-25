package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.server.server.*;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public class FileServer {

    FileServer(final int port, final File rendered) throws IOException{
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
            logger.severe(locale.getString("server.const.failedCreate") + '\n' + Exceptions.getStackTraceAsString(e));
            throw e;
        }

        final ServerExchangeThrottler throttler = new DefaultThrottler();

        // todo: context from config

        final FileHandler staticFileHandler = new StaticFileHandler();
        staticFileHandler.addDirectory(rendered, ByteLoadingOption.WATCHLOAD,true);

        server.createContext("",new ThrottledHandler(staticFileHandler,throttler));

        final FileHandler sysFileHandler = new DefaultFileHandler();
        // todo: add drives & watch add/remove
        server.createContext(Main.getConfigService().getConfig().getString("files_context"),new ThrottledHandler(sysFileHandler,throttler));

        server.start();

    }

}
