package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.render.PageRenderer;
import com.kttdevelopment.webdir.generator.server.HTMLNameAdapter;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class Server {

    private final BiFunction<File,byte[],byte[]> render = new PageRenderer();

    //

    Server(final int port, final File source, final File rendered) throws IOException{
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

        // re-render watch service

        final Path output = rendered.toPath();
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
                // stopped watch service
            }
        }).start();

        // server
        final FileHandler handler = new FileHandler(new HTMLNameAdapter());
        handler.addDirectory(rendered,true);

        server.createContext("",handler);

        server.start();
    }

    private void createWatchService(final WatchService watchService, final Path target){
        try{
            Files.walk(target).filter(path -> path.toFile().isDirectory()).forEach(p -> {
                try{
                    p.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
                    // created watch service
                }catch(final IOException e){
                    // failed to start watch service
                }
            });
        }catch(IOException e){
            // failed to walk
        }
    }

}
