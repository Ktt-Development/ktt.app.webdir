package com.kttdevelopment.webdir.main;

import com.kttdevelopment.webdir.parser.FileConfigPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import static com.kttdevelopment.webdir.main.Logger.*;

public abstract class Directory {

    public static final File includes = new File(com.kttdevelopment.webdir.main.Main.root + "_includes");
    public static final File layouts = new File(com.kttdevelopment.webdir.main.Main.root + "_layouts");
    public static final File extensions = new File(com.kttdevelopment.webdir.main.Main.root + "_extensions");
    public static final File pages = new File(com.kttdevelopment.webdir.main.Main.root + "_pages");

    public static final FileConfigPair index = new FileConfigPair(new File(com.kttdevelopment.webdir.main.Main.root + "index.html"));
    public static final FileConfigPair _404  = new FileConfigPair(new File(com.kttdevelopment.webdir.main.Main.root + "404.html"));

    public static final File site = new File(com.kttdevelopment.webdir.main.Main.root + "site");

    abstract static class Main {

        synchronized static void init(){
            logger.fine(Locale.getString("directory.startInit"));

            createFolder(includes);
            createFolder(layouts);
            createFolder(extensions);
            createFolder(pages);
            createFolder(site);

            logger.fine(Locale.getString("directory.finishedInit"));
        }

        private static void createFolder(final File folder){
            final String name = folder.getName();
            logger.finer("Loading " + name + " folder");
            if(!folder.exists()){
                logger.warning(String.format(Locale.getString("directory.newFolder"),name));
                if(!folder.mkdir()){
                    logger.severe(String.format(Locale.getString("directory.failedNewFolder"),name));
                    throw new RuntimeException(new FileNotFoundException());
                }
            }else if(folder.isDirectory()){
                logger.warning(String.format(Locale.getString("directory.failedNewFolderExists"),name));
                throw new RuntimeException(new FileAlreadyExistsException(folder.getPath()));
            }
            logger.finer("Finished loading " + name + "folder");
        }

    }

}
