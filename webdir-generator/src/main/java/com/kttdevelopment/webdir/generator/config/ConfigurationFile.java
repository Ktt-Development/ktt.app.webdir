package com.kttdevelopment.webdir.generator.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.*;

import java.io.*;
import java.util.Map;

@SuppressWarnings({"rawtypes"})
public class ConfigurationFile extends ConfigurationSectionImpl implements ConfigurationSection {

    public ConfigurationFile(){
        super();
    }

    //

    public synchronized void load(final String yaml) throws YamlException{
        final YamlReader IN = new YamlReader(yaml);
        try{
            config = (Map) IN.read();
        }finally{
            Exceptions.runIgnoreException(IN::close);
        }
    }

    public synchronized void load(final File configFile) throws FileNotFoundException, YamlException{
        load(new FileReader(configFile));
    }

    public synchronized void load(final InputStream stream) throws YamlException{
        load(new InputStreamReader(stream));
    }

    public synchronized void load(final Reader reader) throws YamlException{
        final YamlReader IN = new YamlReader(reader);
        try{
            config = (Map) IN.read();
        }finally{
            Exceptions.runIgnoreException(IN::close);
        }
    }

    @Override
    public String toString(){
        return new toStringBuilder("ConfigurationFile")
            .addObject("configuration",config)
            .toString();
    }

}
