package com.kttdevelopment.webdir.generator.config;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationSectionFile extends ConfigurationSectionImpl {

    public ConfigurationSectionFile(){
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

    //

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
}
