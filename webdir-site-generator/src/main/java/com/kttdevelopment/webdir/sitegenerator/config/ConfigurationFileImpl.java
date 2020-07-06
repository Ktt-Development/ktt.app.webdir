package com.kttdevelopment.webdir.sitegenerator.config;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationFileImpl extends ConfigurationSectionImpl implements ConfigurationFile {

    public ConfigurationFileImpl(){
        super();
    }

    //

    @Override
    public synchronized void setDefault(final ConfigurationFile def){
        this.def = def;
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

    //


    @Override
    public synchronized void reload(){
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(){
        
    }

    @Override
    public void saveDefault(){

    }

    public synchronized void save(final File file){
        final Map cfg = new HashMap(def.toMap());
        cfg.putAll(config);

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(file));
            OUT.write(cfg);
        }catch(final IOException e){
            throw new UncheckedIOException(e);
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final YamlException ignored){ }
        }
    }

    public synchronized void saveDefault(final File file){
        if(!file.exists()){
            YamlWriter OUT = null;
            try{
                OUT = new YamlWriter(new FileWriter(file));
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
