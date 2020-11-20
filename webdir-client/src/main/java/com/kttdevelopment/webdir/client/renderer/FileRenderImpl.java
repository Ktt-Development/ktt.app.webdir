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
