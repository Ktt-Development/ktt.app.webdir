package com.kttdevelopment.webdir.main;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.kttdevelopment.webdir.main._vars.*;

public abstract class Locale {

    abstract static class Main {

        synchronized static void init(){
            final URL  resource = Main.class.getClassLoader().getResource("lang");
            final File lang     = new File(resource.getPath());
            final File[] langs  = lang.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".properties"));

            for(final File iterator : langs){
                try{
                    final Properties properties = new Properties();
                    properties.load(new FileReader(iterator));
                    final java.util.Locale lcl = new java.util.Locale(
                        properties.get("bundle.language.code").toString(),
                        properties.get("bundle.language.region").toString());
                    locale.bundle.put(
                        lcl,
                        ResourceBundle.getBundle(
                            "lang/bundle",
                            new java.util.Locale(
                                properties.get("bundle.language.code").toString(),
                                properties.get("bundle.language.region").toString()
                            )
                        )
                    );
                }catch(final IOException ignored){ }
            }
        }

    }

}
