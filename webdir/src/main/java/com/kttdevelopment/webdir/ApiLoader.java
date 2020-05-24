package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.api.WebDirPlugin;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

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

        try{
            final URLClassLoader loader = new URLClassLoader(pluginUrls.toArray(new URL[0]));
            final Enumeration<URL> resources = loader.findResources("plugin.yml");
            final List<Class<WebDirPlugin>> classes = new ArrayList<>(pluginUrls.size());

            while(resources.hasMoreElements()){
                final URL resource = resources.nextElement();
                // yaml read
                Class<WebDirPlugin> main = null; // name of main class
                try{
                    main = (Class<WebDirPlugin>) loader.loadClass("");
                    // main.getMethod("main").invoke();
                }catch(ClassCastException | ClassNotFoundException e){
                    e.printStackTrace();
                }
                classes.add(main);
            }

        }catch(final IOException e){

        }

        

    }

}
