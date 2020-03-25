package com.kttdevelopment.webdir.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Locale {

    abstract static class Main {

        synchronized static void init(){
            final URL  resource = Main.class.getClassLoader().getResource("lang");
            final File lang     = new File(resource.getPath());
            final File[] langs  = lang.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".properties"));

            for(final File iterator : langs){

                _vars.locale.bundle.put(iterator.getName().substring(7),ResourceBundle.getBundle("lang/bundle"),)
            }

            final ResourceBundle bundle = ResourceBundle.getBundle("lang/bundle",new java.util.Locale("EN","US"));
            System.out.println(bundle.getString("bundle.language.region"));

        }

    }

}
