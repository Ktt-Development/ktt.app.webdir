package com.kttdevelopment.webdir.server.config;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationFileImpl extends ConfigurationSectionImpl implements ConfigurationFile {

    private File configFile = null;

    public ConfigurationFileImpl(){
        super();
    }

    public ConfigurationFileImpl(final File configFile){ // reference only
        super();
        this.configFile = configFile;
    }

    //

    @Override
    public synchronized void setDefault(final ConfigurationFile def){
        this.def = def;
    }

    //

    public synchronized void load(){
        configFile = null;
        config = new HashMap();
    }

    public synchronized void load(final String yaml) throws YamlException{
        configFile = null;

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
        this.configFile = configFile;
    }

    public synchronized void load(final InputStream stream) throws YamlException{
        load(new InputStreamReader(stream));
    }

    public synchronized void load(final Reader reader) throws YamlException{
        configFile = null;

        final YamlReader IN = new YamlReader(reader);
        try{
            config = (Map) IN.read();
        }finally{
            try{ IN.close();
            }catch(final IOException ignored){ }
        }
    }

    //

    @Override
    public synchronized void reload(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(configFile));
            config = (Map) IN.read();
        }catch(FileNotFoundException | YamlException e){
            throw new UncheckedIOException(e);
        }finally{
            if(IN != null)
                try{
                    IN.close();
                }catch(final IOException ignored){ }
        }
    }

    //

    @Override
    public synchronized void save(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        final Map cfg = new HashMap(def.toMap());
        cfg.putAll(config);

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(configFile));
            OUT.write(cfg);
        }catch(final IOException e){
            throw new UncheckedIOException(e);
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final YamlException ignored){ }
        }
    }

    @Override
    public synchronized void saveDefault(){
        if(configFile == null)
            throw new UnsupportedOperationException();

        if(!configFile.exists()){
            YamlWriter OUT = null;
            try{
                OUT = new YamlWriter(new FileWriter(configFile));
                OUT.write(def);
                config = def.toMap();
            }catch(final IOException e){
                throw new UncheckedIOException(e);
            }finally{
                if(OUT != null)
                    try{ OUT.close();
                    }catch(final YamlException ignored){ }
            }
        }
    }

}
