package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public final class RootWatchService {

    private final List<File> drives = Collections.synchronizedList(new ArrayList<>());
    private final FileHandler handler;

    private final String context;

    private final LocaleService locale;
    private final Logger logger;

    public RootWatchService(final FileHandler handler, final String context){
        this.locale = Main.getLocale();
        this.logger = Main.getLogger(locale.getString("page-renderer.name"));

        this.handler = handler;
        this.context = context;
        check();
    }

    public synchronized final void check(){
        final FileSystemView fileSys = FileSystemView.getFileSystemView();
        final File[] allRoots = File.listRoots();
        final List<File> loadedDrives = new ArrayList<>();
        for(final File root : allRoots)
            if(fileSys.isDrive(root))
                loadedDrives.add(root);

        for(final File drive : loadedDrives){
            if(!drives.contains(drive)){
                drives.add(drive);
                onAddedEvent(drive);
            }
        }

        for(final File drive : drives){
            if(!loadedDrives.contains(drive)){
                drives.remove(drive);
                onRemovedEvent(drive);
            }
        }
    }

    public synchronized final void onAddedEvent(final File file){
        handler.addDirectory(context, file, true);
        logger.fine(locale.getString("server.roots.added", file.getName()));
    }

    public synchronized final void onRemovedEvent(final File file){
        handler.removeDirectory(context, file);
        logger.fine(locale.getString("server.roots.removed", file.getName()));
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("drives", drives)
            .addObject("context", context)
            .toString();
    }

}
