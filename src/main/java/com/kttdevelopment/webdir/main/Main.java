package com.kttdevelopment.webdir.main;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

public class Main {

    static final String root = "";

    private static SimpleHttpServer server;

    public static void main(String[] args){
        Logger.Main.init();
        Locale.Main.init();
        Config.Main.init();
        Directory.Main.init();
    }

}
