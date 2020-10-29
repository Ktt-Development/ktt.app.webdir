package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;

import java.io.File;
import java.util.Map;

public abstract class FileRender {

    public abstract File getInputFile();

    public abstract File getOutputFile();

    public abstract void setOutputFile(final File file);

    //

    public abstract Map<String,? super Object> getFrontMatter();

    public abstract boolean hasFrontMatter();

    //

    public abstract String getContentAsString();

    public abstract byte[] getContentAsBytes();

    //

    public abstract SimpleHttpServer getServer();

    public abstract SimpleHttpExchange getHttpExchange();

}
