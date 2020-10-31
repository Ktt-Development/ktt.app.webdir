package com.kttdevelopment.webdir.client;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.simplehttpserver.handler.ThrottledHandler;
import com.kttdevelopment.webdir.client.server.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;

public final class FileServer {

    private final SimpleHttpServer server;

    FileServer(final String port) throws IOException{
        // todo: logging

        try{
            this.server = SimpleHttpServer.create(Integer.parseInt(port));
        }catch(IOException e){
            e.printStackTrace();

            throw e;
        }

        final DefaultThrottler throttler = new DefaultThrottler();
        final DefaultFileHandler handler = new DefaultFileHandler(Main.getPageRenderingService(), server);

        server.createContext("", new ThrottledHandler(handler, throttler));
        handler.addDirectory(new File(Main.getConfig().string(ConfigService.OUTPUT)));

        server.start();
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("server", server)
            .toString();
    }

}
