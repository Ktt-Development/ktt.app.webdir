package com.kttdevelopment.webdir;

public abstract class Server {

    private static boolean init = false;
    public synchronized static void main(){
        if(init) return; else init = true;


    }

}
