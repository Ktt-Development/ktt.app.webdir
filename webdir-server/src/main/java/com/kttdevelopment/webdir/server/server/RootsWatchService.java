package com.kttdevelopment.webdir.server.server;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class RootsWatchService{

    private final long delay;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stop = new AtomicBoolean(false);

    private AtomicReference<File[]> roots;

    public RootsWatchService(final long delay){
        this.delay = delay;
    }

    public synchronized final void start(){
        if(!running.get())
        new Thread(() -> {
            running.set(true);
            while(!stop.get()){
                final FileSystemView fileSys = FileSystemView.getFileSystemView();
                final File[] roots = fileSys.getRoots();
                final List<File> drives = new ArrayList<>();
                for(final File root : roots)
                    if(fileSys.isDrive(root))
                        drives.add(root);
                RootsWatchService.this.roots.set( drives.toArray(new File[0]));
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

    public final File[] listRoots(){
        return roots.get();
    }

}
