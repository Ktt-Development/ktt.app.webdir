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

package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public final class FileRenderImpl extends FileRender {

    private final File input;
    private File output;
    private final Map<String,? super Object> frontMatter;
    private String asString;
    private byte[] asBytes;

    private SimpleHttpServer server = null;
    private SimpleHttpExchange exchange = null;

    public FileRenderImpl(final File input, final File output, final Map<String,? super Object> frontMatter, final byte[] bytes){
        this.input       = input;
        this.output      = output;
        this.frontMatter = frontMatter;
        this.asString    = bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
        this.asBytes     = bytes;
    }

    public FileRenderImpl(final File input, final File output, final Map<String,? super Object> frontMatter, final byte[] bytes, final SimpleHttpServer server, final SimpleHttpExchange exchange){
        this(input,output,frontMatter,bytes);
        this.server     = server;
        this.exchange   = exchange;
    }

    @Override
    public final File getInputFile(){
        return input;
    }

    @Override
    public final File getOutputFile(){
        return output;
    }

    @Override
    public synchronized void setOutputFile(final File file){
        this.output = file;
    }

    public Map<String,? super Object> getFrontMatter(){
        return frontMatter;
    }

    @Override
    public final boolean hasFrontMatter(){
        return frontMatter == null;
    }

    final void setBytes(final byte[] bytes){
        this.asString = bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
        this.asBytes  = bytes;
    }

    @Override
    public final String getContentAsString(){
        return asString;
    }

    @Override
    public final byte[] getContentAsBytes(){
        return asBytes;
    }

    @Override
    public final SimpleHttpServer getHttpServer(){
        return server;
    }

    @Override
    public final SimpleHttpExchange getHttpExchange(){
        return exchange;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("input", input)
            .addObject("output", output)
            .addObject("frontMatter", frontMatter)
            .addObject("asString", asString)
            .addObject("asBytes", Arrays.toString(asBytes))
            .addObject("server", server)
            .addObject("exchange", exchange)
            .toString();
    }

}
