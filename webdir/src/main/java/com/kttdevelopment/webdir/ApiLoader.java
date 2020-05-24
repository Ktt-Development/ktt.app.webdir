package com.kttdevelopment.webdir;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.kttdevelopment.webdir.Logger.*;

public abstract class ApiLoader {

    private static final File pluginsFolder = new File(Application.parent + '\\' + "plugins");

    private static boolean init = false;
    public synchronized static void main(){
        if(init) return; else init = true;

        logger.info("[ApiLoader] Started api loader initialization"); // locale

        final File[] plugins = pluginsFolder.listFiles((dir, name) -> !dir.isDirectory() && name.endsWith(".jar"));
        final List<URL> pluginUrls = new ArrayList<>();

        for(final File plugin : plugins){
            try{ pluginUrls.add(plugin.toURI().toURL());
            }catch(final MalformedURLException e){
                logger.severe("[ApiLoader] Failed to load " + plugin.getName() + " (malformed url)"); // locale
            }
        }

        

    }

}
