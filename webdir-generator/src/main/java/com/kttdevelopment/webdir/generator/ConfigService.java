package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionFile;

import java.io.File;

public final class ConfigService {

    private final ConfigurationFile config = new ConfigurationSectionFile();

    public final ConfigurationFile getConfig(){
        return config;
    }

    private final File configFile;

    ConfigService(final File configFile, final String defaultConfigResource){
        this.configFile = configFile;


    }

}
