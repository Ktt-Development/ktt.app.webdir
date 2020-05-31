package com.kttdevelopment.webdir.config;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationFileImpl extends ConfigurationSectionImpl implements ConfigurationFile {

    private final File configFile;

    public ConfigurationFileImpl(){
        super();
        configFile = null;
    }

    public ConfigurationFileImpl(final File configFile, boolean skipRead){
        super();
        this.configFile = skipRead ? configFile : null;
    }

    public ConfigurationFileImpl(final File configFile) throws FileNotFoundException, YamlException{
        super();
        this.configFile = configFile;
        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException ignored){ }
        }
    }

    public ConfigurationFileImpl(final Reader reader) throws IOException {
        super();
        configFile = null;

        final YamlReader IN = new YamlReader(reader);
        try{
            config = (Map) IN.read();
        }finally{
            try{ IN.close();
            }catch(final IOException ignored){ }
        }
    }

    public ConfigurationFileImpl(final InputStream stream) throws IOException {
        this(new InputStreamReader(stream));
    }

    public ConfigurationFileImpl(final String yaml) throws YamlException{
        super();
        configFile = null;

        final YamlReader IN = new YamlReader(yaml);
        try{
            config = (Map) IN.read();
        }finally{
            try{ IN.close();
            }catch(IOException ignored){ }
        }
    }

    //

    @Override
    public synchronized final void setDefault(final ConfigurationFile def){
        this.def = def;
    }

    //

    @Override
    public synchronized final void reload(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
        }catch(FileNotFoundException | YamlException ignored){
            // don't reload
        }finally{
            if(IN != null)
                try{
                    IN.close();
                }catch(final IOException ignored){ }
        }
    }

    //

    @Override
    public synchronized final void save(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        final Map cfg = new HashMap(def.toMap());
        cfg.putAll(config);

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(cfg);
        }catch(final IOException ignored){
            // don't save
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final YamlException ignored){ }
        }
    }

    @Override
    public synchronized final void saveDefault(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        if(!configFile.exists()){
            YamlWriter OUT = null;
            try{
                OUT = new YamlWriter(new FileWriter(configFile));
                OUT.write(def);
                config = def.toMap();
            }catch(final IOException ignored){
                // don't save
            }finally{
                if(OUT != null)
                    try{ OUT.close();
                    }catch(final YamlException ignored){ }
            }
        }
    }

}
