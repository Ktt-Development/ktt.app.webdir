package com.kttdevelopment.webdir;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class LinkedFile {

    private final File file;
    private final File config;

    public LinkedFile(final File file){
        this.file = file;
        config = new File(FilenameUtils.removeExtension(file.getName()) + "yml");
    }

    public final boolean exists(){
        return file.exists();
    }

    public final boolean hasConfig(){
        return config.exists();
    }

}
