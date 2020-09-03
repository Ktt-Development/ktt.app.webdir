package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class FileRenderImpl extends FileRender {

    private final File input;
    private File output;
    private final ConfigurationSection yamlFrontMatter;
    private final boolean hasFrontMatter;
    private final String asString;
    private final byte[] asBytes;

    private SimpleHttpServer server = null;
    private SimpleHttpExchange exchange = null;

    public FileRenderImpl(final File input, final File output, final ConfigurationSection yamlFrontMatter, final byte[] bytes){
        this.input           = input;
        this.output          = output;
        this.yamlFrontMatter = yamlFrontMatter;
        this.hasFrontMatter  = yamlFrontMatter == null;
        this.asString        = new String(bytes, StandardCharsets.UTF_8);
        this.asBytes         = bytes;
    }

    public FileRenderImpl(final File input, final File output, final ConfigurationSection yamlFrontMatter, final byte[] bytes, final SimpleHttpServer server, final SimpleHttpExchange exchange){
        this(input,output,yamlFrontMatter,bytes);
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
    public synchronized final void setOutputFile(final File file){
        output = file;
    }

    @Override
    public final ConfigurationSection getYamlFrontMatter(){
        return yamlFrontMatter;
    }

    @Override
    public final boolean hasFrontMatter(){
        return hasFrontMatter;
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
    public final SimpleHttpServer getServer(){
        return server;
    }

    @Override
    public final SimpleHttpExchange getHttpExchange(){
        return exchange;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder("FileRender")
            .addObject("input",input)
            .addObject("output",output)
            .addObject("yamlFrontMatter",yamlFrontMatter)
            .addObject("hasFrontMatter",hasFrontMatter)
            .addObject("asString",asString)
            .addObject("asBytes",asBytes)
            .addObject("server",server)
            .addObject("exchange",exchange)
            .toString();
    }

}
