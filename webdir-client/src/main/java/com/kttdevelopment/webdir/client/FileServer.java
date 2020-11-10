package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.ThrottledHandler;
import com.kttdevelopment.webdir.client.server.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
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
        final DefaultFileHandler handler = new DefaultFileHandler(Main.getPageRenderingService(), server);

        server.createContext("", new ThrottledHandler(handler, throttler));
        handler.addDirectory(new File(config.string(ConfigService.OUTPUT)), "", true);

        server.start();
        logger.info(locale.getString("server.constructor.finish"));
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("server", server)
            .toString();
    }

}
