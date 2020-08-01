package com.kttdevelopment.webdir.server.server;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RootsWatchService{

    private final long delay;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stop = new AtomicBoolean(false);

    private final List<File> drives = Collections.synchronizedList(new ArrayList<>());

    public RootsWatchService(final long delay){
        this.delay = delay;
    }

    public synchronized final void start(){
        if(!running.get())
        new Thread(() -> {
            running.set(true);
            while(!stop.get()){
                final FileSystemView fileSys = FileSystemView.getFileSystemView();
                final File[] allRoots = fileSys.getRoots();
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
                try{
                    Thread.sleep(delay);
                }catch(final InterruptedException ignored){
                    // todo: logging
                    running.set(false);
                }
            }
        }).start();
    }

    public synchronized final void stop(){
        stop.set(true);
        running.set(false);
    }

    //

    public void onRemovedEvent(final File file){

    }

    public void onAddedEvent(final File file){

    }

}
