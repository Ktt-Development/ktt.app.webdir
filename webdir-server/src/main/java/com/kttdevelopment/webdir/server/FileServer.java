package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.server.server.*;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public final class FileServer {

    FileServer(final int port, final File defaults, final File source, final File output) throws IOException{
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

        logger.info(locale.getString("server.const.createStaticHandler"));
        final FileHandler staticFileHandler = new StaticFileHandler(defaults,source,output);
        staticFileHandler.addDirectory(output, ByteLoadingOption.PRELOAD,true);

        server.createContext("",new ThrottledHandler(staticFileHandler,throttler));

        logger.info(locale.getString("server.const.createFileHandler"));
        final FileHandler defaultFileHandler = new DefaultFileHandler(defaults);
        // todo: add drive & watch add/remove

        server.createContext(ContextUtil.getContext(Main.getConfigService().getConfig().getString(ServerVars.Config.filesContextKey,ServerVars.Config.defaultFilesContext),true,false),new ThrottledHandler(defaultFileHandler,throttler));

        server.start();
        logger.info(locale.getString("server.const.start"));
        logger.info(locale.getString("server.const.loaded"));
    }

}
