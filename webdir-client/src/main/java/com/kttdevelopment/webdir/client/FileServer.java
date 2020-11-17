package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.client.server.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class FileServer {

    private final SimpleHttpServer server;

    FileServer(final String port) throws IOException{
        final LocaleService locale = Main.getLocale();
        final YamlMapping config   = Main.getConfig();
        final Logger logger        = Main.getLogger(locale.getString("server.name"));

        logger.info(locale.getString("server.constructor.start"));

        try{
            this.server = SimpleHttpServer.create(Integer.parseInt(port));
        }catch(final IOException e){
            logger.severe(locale.getString("server.constructor.port") + LoggerService.getStackTraceAsString(e));
            throw e;
        }

        final DefaultThrottler throttler = new DefaultThrottler();

        final DefaultSiteHandler _siteHandler = new DefaultSiteHandler(Main.getPageRenderingService(), server, new File(config.string(ConfigService.OUTPUT), config.string(ConfigService.F04)));
        _siteHandler.addDirectory(new File(config.string(ConfigService.OUTPUT)), "", true);
        final DefaultFileHandler filesHandler = new DefaultFileHandler(Main.getPageRenderingService(), server);
        final FileHandler rawHandler          = new RawFileHandler();

        // todo: add 404 here ↓
        server.createContext("", new ThrottledHandler(_siteHandler, throttler));
        server.createContext(Main.getConfig().string(ConfigService.CONTEXT), new ThrottledHandler(filesHandler, throttler));
        server.createContext(Main.getConfig().string(ConfigService.RAW), new ThrottledHandler(rawHandler, throttler));

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        logger.info(locale.getString("server.constructor.finish"));
    }

    public final SimpleHttpServer getServer(){
        return server;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("server", server)
            .toString();
    }

}
