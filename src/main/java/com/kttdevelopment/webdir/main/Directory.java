package com.kttdevelopment.webdir.main;

import static com.kttdevelopment.webdir.main.Logger.*;

public abstract class Directory {

    abstract static class Main {

        synchronized static void init(){
            logger.fine("Started directory init.");

            logger.fine("Finished directory init.");
        }

    }

}
