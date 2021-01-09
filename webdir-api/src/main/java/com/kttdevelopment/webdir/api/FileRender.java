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

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.File;
import java.util.Map;

/**
 * Represents a source/output file render.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Ktt Development
 */
public abstract class FileRender {

    /**
     * Returns the file in the sources folder.
     *
     * @return source file
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract File getInputFile();

    /**
     * Returns the output file.
     *
     * @return output file
     *
     * @see #setOutputFile(File)
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract File getOutputFile();

    /**
     * Sets the new output file, by default it is the same as the source file but in the output folder. Set to <code>null</code> to not create an output file.
     *
     * @param file output file
     *
     * @see #getOutputFile()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract void setOutputFile(final File file);

    //

    /**
     * Returns the yaml front matter as a map.
     *
     * @return front matter
     *
     * @see #hasFrontMatter()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract Map<String,? super Object> getFrontMatter();

    /**
     * Returns if the file has yaml front matter (if the file contains front matter dashes).
     *
     * @return if file has front matter
     *
     * @see #getFrontMatter()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract boolean hasFrontMatter();

    //

    /**
     * Returns current render as a string.
     *
     * @return current render as string
     *
     * @see #getContentAsBytes()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract String getContentAsString();

    /**
     * Returns current render as bytes.
     *
     * @return current render as bytes
     *
     * @see #getContentAsString()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract byte[] getContentAsBytes();

    //

    /**
     * Returns the http server or null if there is none running.
     *
     * @return http server
     *
     * @see SimpleHttpServer
     * @see #getHttpExchange()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract SimpleHttpServer getHttpServer();

    /**
     * Returns the http exchange or null if there is none.
     *
     * @return http exchange
     *
     * @see SimpleHttpExchange
     * @see #getHttpServer()
     * @since 1.0.0
     * @author Ktt Development
     */
    public abstract SimpleHttpExchange getHttpExchange();

}
