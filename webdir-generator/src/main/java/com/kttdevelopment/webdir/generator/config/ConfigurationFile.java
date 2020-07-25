package com.kttdevelopment.webdir.generator.config;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

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
            try{ IN.close();
            }catch(final IOException ignored){ }
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
            try{ IN.close();
            }catch(final IOException ignored){ }
        }
    }

}
