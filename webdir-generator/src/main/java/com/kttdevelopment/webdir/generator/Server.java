package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.server.HTMLNameAdapter;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.*;
import java.util.logging.Logger;

public final class Server {

    private final String source, output;
    private final int port;

    private final SimpleHttpServer server;

    Server(final int port, final File source, final File output) throws IOException{
        this.source = source.getAbsolutePath();
        this.output = output.getAbsolutePath();

        final LocaleService locale  = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("server"));
        logger.info(locale.getString("server.const"));

        try{
            server = SimpleHttpServer.create(!Vars.Test.server ? port : Vars.Test.port);
            this.port = server.getAddress().getPort();
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

        // re-render watch service
        logger.info(locale.getString("server.const.watch.init"));
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        final Path watching = source.toPath();

        createWatchService(watchService,watching);

        new Thread(() -> {
            WatchKey key;
            try{
                while((key = watchService.take()) != null){
                    for(WatchEvent<?> event : key.pollEvents()){
                        final Path context = (Path) event.context();
                        final Path modified = Paths.get(watching.toString(),context.toString());
                        final File file = modified.toFile();

                        if(file.isDirectory() && event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                            createWatchService(watchService,modified);
                        else
                            Main.getPageRenderingService().render(file);
                    }
                    key.reset();
                }
            }catch(final InterruptedException e){
                logger.severe(locale.getString("server.const.watch.interrupt") + '\n' + Exceptions.getStackTraceAsString(e));
            }
        }).start();
        logger.info(locale.getString("server.const.watch.loaded"));

        // server
        final FileHandler handler = new FileHandler(new HTMLNameAdapter());
        handler.addDirectory(output,"",true);

        server.createContext("",handler);

        server.start();
        logger.info(locale.getString("server.const.start"));
        logger.info(locale.getString("server.const.loaded"));
    }

    private void createWatchService(final WatchService watchService, final Path target){
        final LocaleService locale = Vars.Main.getLocaleService();
        final Logger logger        = Vars.Main.getLoggerService().getLogger(locale.getString("server"));

        try{
            Files.walk(target).filter(path -> path.toFile().isDirectory()).forEach(p -> {
                try{
                    logger.finest(locale.getString("server.debug.createWatchService",p.toFile().getAbsolutePath()));
                    p.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
                    // created watch service debug
                }catch(final IOException e){
                    logger.severe(locale.getString("server.createWatchService.failedRegister", p) + '\n' + Exceptions.getStackTraceAsString(e));
                }
            });
        }catch(final IOException e){
            logger.severe(locale.getString("server.createWatchService.failedWalk", target) + '\n' + Exceptions.getStackTraceAsString(e));
        }
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("Server")
            .addObject("source",source)
            .addObject("output",output)
            .addObject("port",port)
            .addObject("server",server)
            .toString();
    }

}
