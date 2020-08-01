package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.*;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.server.server.*;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public final class FileServer {

    private final RootsWatchService watchService;

    private final int port;
    private final File defaults, source, output;

    FileServer(final int port, final File defaults, final File source, final File output) throws IOException{
        this.port = port;
        this.defaults = defaults;
        this.source = source;
        this.output = output;

        final LocaleService locale = Vars.Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("server"));
        final SimpleHttpServer server;

        logger.fine(locale.getString("server.debug.const.port", port));
        try{
            server = SimpleHttpServer.create(port);
        }catch(final BindException e){
            logger.severe(locale.getString("server.const.blockedPort", port));
            throw e;
        }catch(final IllegalArgumentException e){
            logger.severe(locale.getString("server.const.invalidPort", port));
            throw e;
        }catch(final IOException e){
            logger.severe(locale.getString("server.const.failedCreate") + '\n' + Exceptions.getStackTraceAsString(e));
            throw e;
        }

        final ServerExchangeThrottler throttler = new DefaultThrottler();

        logger.info(locale.getString("server.const.createStaticHandler"));
        final FileHandler staticFileHandler = new StaticFileHandler(defaults, source, output);
        staticFileHandler.addDirectory(output, ByteLoadingOption.PRELOAD, true);

        server.createContext("", new ThrottledHandler(staticFileHandler, throttler));

        logger.info(locale.getString("server.const.createFileHandler"));

        final String fileContext = Main.getConfigService().getConfig().getString(ServerVars.Config.filesContextKey, ServerVars.Config.defaultFilesContext);
        watchService = new RootsWatchService(1000 * 5) {

            @Override
            public synchronized final void onAddedEvent(final File file){
                final String root = file.toPath().getRoot().toString();
                final String context = ContextUtil.joinContexts(true, false, fileContext, root);
                final DefaultFileHandler handler = new DefaultFileHandler(defaults);

                handler.addDirectory(file, "");
                server.createContext(context, new ThrottledHandler(handler, throttler));
                logger.fine(locale.getString("server.debug.const.rootAdd",file.getAbsolutePath(),context));
            }

            @Override
            public synchronized final void onRemovedEvent(final File file){
                final String root = file.toPath().getRoot().toString();
                final String context = ContextUtil.joinContexts(true, false, fileContext, root);
                server.removeContext(context);
                logger.fine(locale.getString("server.debug.const.rootRemove",file.getAbsolutePath(),context));
            }

        };

        watchService.start();
        logger.info(locale.getString("server.const.startFileHandler"));

        server.start();
        logger.info(locale.getString("server.const.start"));
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("FileServer")
            .addObject("port",port)
            .addObject("defaults",defaults)
            .addObject("source",source)
            .addObject("output",output)
            .addObject("watchService",watchService)
            .toString();
    }

}
