package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.IOException;

public abstract class Server {

    private static SimpleHttpServer server;

    public static void main() throws IOException{
        server = SimpleHttpServer.create();
    }

}
