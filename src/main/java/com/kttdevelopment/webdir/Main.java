package com.kttdevelopment.webdir;

import java.io.IOException;

public abstract class Main {

    public synchronized static void main(String[] args) throws IOException{
        Logger.main();
        Locale.main();
        Config.main();
        Locale.main();
        Server.main();
    }

}
