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

    private final List<String> drives = Collections.synchronizedList(new ArrayList<>());
    private final FileHandler handler;

    private final LocaleService locale;
    private final Logger logger;

    public RootWatchService(final FileHandler handler){
        this.locale = Main.getLocale();
        this.logger = Main.getLogger(locale.getString("page-renderer.name"));

        this.handler = handler;
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
            if(!drives.contains(drive.getAbsolutePath())){
                drives.add(drive.getAbsolutePath());
                onAddedEvent(drive);
            }
        }

        for(final String drive : drives){
            for(final File loadedDrive : loadedDrives){
                if(loadedDrive.getAbsolutePath().equalsIgnoreCase(drive)){
                    drives.remove(drive);
                    onRemovedEvent(loadedDrive);
                }
            }
        }
    }

    public synchronized final void onAddedEvent(final File file){
        handler.addDirectory(file, true);
        logger.fine(locale.getString("server.roots.added", file.getPath()));
    }

    public synchronized final void onRemovedEvent(final File file){
        handler.removeDirectory(file);
        logger.fine(locale.getString("server.roots.removed", file.getPath()));
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("drives", drives)
            .toString();
    }

}
