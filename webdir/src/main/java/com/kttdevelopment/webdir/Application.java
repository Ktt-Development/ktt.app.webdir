package com.kttdevelopment.webdir;

import java.io.File;

public abstract class Application {

    public static final String parent = new File("").getAbsolutePath();

    public synchronized static void main(String[] args){
        Logger.main();
        Config.main();
        Locale.main();
        ApiLoader.main();
        Server.main();
    }

}
