package com.kttdevelopment.webdir.client.plugins;

import java.io.File;
import java.io.FilenameFilter;

public final class JarFilter implements FilenameFilter {

    @Override
    public boolean accept(final File dir, final String name){
        return dir.isFile() && name.toLowerCase().endsWith(".jar");
    }

}
