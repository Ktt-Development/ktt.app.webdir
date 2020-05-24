package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.IOException;

public final class Server {

    private static boolean init = false;
    Server(){
        if(init) return; else init = true;

        try{
            final SimpleHttpServer server = SimpleHttpServer.create();

            server.bind(Integer.parseInt(Application.config.get("port").toString()));
        }catch(NumberFormatException | IOException e){
            e.printStackTrace();
        }
    }

}
