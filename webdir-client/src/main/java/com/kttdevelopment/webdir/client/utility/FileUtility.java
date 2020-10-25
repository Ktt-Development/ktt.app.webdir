package com.kttdevelopment.webdir.client.utility;

import java.io.File;

public abstract class FileUtility {

    public static String getFileName(final String filename){
        return filename.contains(".")
           ? filename.substring(0,filename.lastIndexOf('.'))
           : filename;
    }

    public static String getFileExtension(final String filename){
        return filename.contains(".")
            ? filename.substring(filename.lastIndexOf('.') + 1)
            : "";
    }

    public static File getFreeFile(final File file){
        if(!file.exists())
            return file;

        final String filename  = file.getName();
        long copyno = 0;
        File copy;

        while((copy = new File(file.getParentFile(),String.format("%s (%s).%s",getFileName(filename),copyno,getFileExtension(filename)))).exists())
            copyno++;
        return copy;
    }


}
