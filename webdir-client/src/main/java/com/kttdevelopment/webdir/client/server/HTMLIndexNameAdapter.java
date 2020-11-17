package com.kttdevelopment.webdir.client.server;

import java.io.File;

public final class HTMLIndexNameAdapter extends HTMLNameAdapter {

    @Override
    public final String getName(final File file){ // if directory look for index.html inside of folder
        return file.isDirectory() && new File(file, "index.html").exists() ? file.getName() + '/' + "index.html" : super.getName(file);
    }

}
