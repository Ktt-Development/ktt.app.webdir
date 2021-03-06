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

package com.kttdevelopment.webdir.api;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * A renderer determines how context will appear.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Ktt Development
 */
public class Renderer {

    private final String permission;

    /**
     * Creates a renderer with no required permission.
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public Renderer(){
        this.permission = null;
    }

    /**
     * Creates a renderer with a required permission.
     *
     * @param permission permission required to use handler
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public Renderer(final String permission){
        this.permission = permission;
    }

    /**
     * Returns the permission required to use the renderer or null if no permission is required.
     *
     * @return required permission
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public final String getPermission(){
        return permission;
    }

    /**
     * Returns whether the renderer can be used with the type of file.
     *
     * @param file file to test
     *
     * @return if the renderer can be used
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public boolean test(final File file){
        return true;
    }

    //

    /**
     * Renders content from a file.
     *
     * @param render file information, see {@link FileRender}
     * @return rendered content
     *
     * @see FileRender
     * @since 1.0.0
     * @author Ktt Development
     */
    public byte[] render(final FileRender render){
        return render.getContentAsBytes();
    }

    /**
     * Converts a string to a byte array with UTF-8 encoding.
     *
     * @param string string to encode
     * @return string as bytes
     *
     * @since 1.0.0
     * @author KTt Development
     */
    public final byte[] asBytes(final String string){
        return string.getBytes(StandardCharsets.UTF_8);
    }

    //

    @Override
    public String toString(){
        return "Renderer{" +
               "permission='" + permission + '\'' +
               '}';
    }

}
