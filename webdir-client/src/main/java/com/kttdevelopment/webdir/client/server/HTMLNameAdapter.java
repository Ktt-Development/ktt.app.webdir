package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.FileHandlerAdapter;

import java.io.File;

public final class HTMLNameAdapter implements FileHandlerAdapter {

    private final int len = ".html".length();

    @Override
    public final String getName(final File file){
        final String name = file.getName();
        final String rhtml = name.endsWith(".html") ? name.substring(0, name.length()-len) : name;
        return rhtml.isBlank() || rhtml.equals(".html") ? name : rhtml;
    }

}
