/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
        final List<String> loadedDrivesAsStr = new ArrayList<>();
        for(final File root : allRoots)
            if(fileSys.isDrive(root)){
                loadedDrives.add(root);
                loadedDrivesAsStr.add(root.getPath());
            }

        for(final File drive : loadedDrives){
            if(!drives.contains(drive.getPath())){
                drives.add(drive.getPath());
                onAddedEvent(drive);
            }
        }

        for(final String drive : drives)
            if(!loadedDrivesAsStr.contains(drive))
                onRemovedEvent(new File(drive));
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
