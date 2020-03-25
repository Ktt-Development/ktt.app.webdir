package com.kttdevelopment.webdir.main;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

public class Main {

    private static SimpleHttpServer server;

    public static void main(String[] args){
        Logger.Main.init();
        Config.Main.init();
        Locale.Main.init();
    }

}
