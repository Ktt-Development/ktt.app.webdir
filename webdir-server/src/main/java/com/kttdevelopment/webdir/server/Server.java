package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.server.server.*;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public final class Server {

    private final SimpleHttpServer server;

    public final SimpleHttpServer getServer(){
        return server;
    }

    //

    Server() throws IOException{
        final LocaleService locale = Application.getLocaleService();
        final ConfigService config = Application.getConfigService();
        final Logger logger = Logger.getLogger(locale.getString("server"));
        logger.info(locale.getString("server.init.start"));

        // port bind
        final int port = config.getConfig().getInteger("key");
        try{
            server = SimpleHttpServer.create();
            server.bind(port);
        }catch(final IllegalArgumentException | IOException e){
            logger.severe(
                e instanceof IllegalArgumentException
                ? locale.getString("server.init.badPort",port)
                : e instanceof BindException
                  ? locale.getString("server.init.portTaken",port)
                  : locale.getString("server.init.failed") + '\n' + LoggerService.getStackTraceAsString(e)
            );
            throw e;
        }
        // init

        final ServerExchangeThrottler throttler = new DefaultThrottler();

        final FileHandler staticHandler = new StaticFileHandler();
        staticHandler.addDirectory(new File(Application.parent + '\\' + config.getConfig().getString("source")), ByteLoadingOption.WATCHLOAD);
        server.createContext("",staticHandler);

        server.createContext("",new ThrottledHandler(staticHandler,throttler));

        //

        final FileHandler fileHandler = new DefaultFileHandler();
        for(final File file : File.listRoots())
            fileHandler.addDirectory(file);

        server.createContext(config.getConfig().getString("files"),new ThrottledHandler(fileHandler,throttler));

        // start

        logger.info(locale.getString("server.init.finished"));
    }

}
