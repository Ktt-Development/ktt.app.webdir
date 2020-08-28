package com.kttdevelopment.webdir.client;

import java.io.File;
import java.util.Objects;

public final class ConfigService {

    private final File configFile;
    private final String[] args;

    public ConfigService(final File configFile, final String[] args){
        Objects.requireNonNull(configFile);
        this.configFile = configFile;
        this.args       = args;


    }

}
