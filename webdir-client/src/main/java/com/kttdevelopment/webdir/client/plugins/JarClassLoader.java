package com.kttdevelopment.webdir.client.plugins;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarClassLoader {

    private final File file;
    private final JarFile jar;

    public JarClassLoader(final File jarFile) throws IOException{
        this.file = jarFile;
        this.jar  = new JarFile(jarFile);
    }

    private static final int len = ".class".length();

    public final URLClassLoader load() throws MalformedURLException, ClassNotFoundException{
        final URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        final Enumeration<JarEntry> entries = jar.entries();

        while(entries.hasMoreElements()){
            // load all java .class files from jar
            final JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            if(!entry.isDirectory() && name.endsWith(".class"))
                loader.loadClass(name.substring(0,name.length()-len).replace('/','.'));
        }
        return loader;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("file",file)
            .addObject("jarFile",jar)
            .toString();
    }

}
