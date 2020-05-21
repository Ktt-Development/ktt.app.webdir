package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.IOException;
import java.util.concurrent.Executors;

public abstract class Server {

    private static SimpleHttpServer server;

    public static void main() throws IOException{
        server = SimpleHttpServer.create(Integer.parseInt(Config.get("port").toString()));
        server.setExecutor(Executors.newCachedThreadPool());


        server.start();
    }

}
