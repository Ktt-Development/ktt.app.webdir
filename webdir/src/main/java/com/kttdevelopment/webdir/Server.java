package com.kttdevelopment.webdir;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.IOException;

public abstract class Server {

    private static boolean init = false;
    public synchronized static void main(){
        if(init) return; else init = true;

        try{
            final SimpleHttpServer server = SimpleHttpServer.create();

            server.bind(Integer.parseInt(Config.get("port").toString()));
        }catch(NumberFormatException | IOException e){
            e.printStackTrace();
        }
    }

}
