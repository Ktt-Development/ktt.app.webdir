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
