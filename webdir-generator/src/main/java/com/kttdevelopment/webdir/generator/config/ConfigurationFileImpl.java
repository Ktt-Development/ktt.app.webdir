package com.kttdevelopment.webdir.generator.config;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.ExceptionSupplier;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationFileImpl extends ConfigurationSectionImpl implements ConfigurationFile {

    private File file;

    public ConfigurationFileImpl(){
        super();
    }

    public ConfigurationFileImpl(final File file){
        super();
        }

    public synchronized void saveToFile(final File file) throws IOException{
        final Map cfg = new HashMap(def.toMap());
        cfg.putAll(config);

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(file));
            OUT.write(cfg);
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final YamlException ignored){ }
        }
    }

    @Override
    public void setDefault(final ConfigurationSection def){
        this.def = def;
    }

    @Override
    public synchronized boolean reload(){
        try{
            if(file == null) throw new UnsupportedOperationException();
            load(file);
            return true;
        }catch(final Exception e){
            Exceptions.throwUnchecked(e);
            return false;
        }
    }

    @Override
    public synchronized boolean save(){
        try{
            if(file == null) throw new UnsupportedOperationException();
            saveToFile(file);
            return true;
        }catch(final Exception e){
            Exceptions.throwUnchecked(e);
            return false;
        }
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
        file = configFile;
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
