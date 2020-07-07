package com.kttdevelopment.webdir.generator.config;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.File;

public class ConfigurationFileImpl extends ConfigurationSectionFile implements ConfigurationFile {

    private final File file;

    public ConfigurationFileImpl(final File file){
        super();
        this.file = file;
    }

    @Override
    public final void setDefault(final ConfigurationFile def){
        this.def = def;
    }

    @Override
    public final boolean reload(){
        return Exceptions.requireNonExceptionElse(
            () -> {
                load(file);
                return true;
            },
            false
        );
    }

    @Override
    public final boolean save(){
        return Exceptions.requireNonExceptionElse(
            () -> {
                saveToFile(file);
                return true;
            },
            false
        );
    }

}
