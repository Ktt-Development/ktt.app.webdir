package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;

public final class RootWatchService {

    private final List<File> drives = Collections.synchronizedList(new ArrayList<>());
    private final FileHandler handler;

    private final String context;

    public RootWatchService(final FileHandler handler, final String context){
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
        handler.addDirectory(context, file);
    }

    public synchronized final void onRemovedEvent(final File file){
        // TBA
        // handler.removeDirectory(file);
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("drives", drives)
            .addObject("context", context)
            .toString();
    }

}
