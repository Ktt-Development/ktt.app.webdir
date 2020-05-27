package com.kttdevelopment.webdir.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ConfigurationFileImpl extends ConfigurationSectionImpl implements ConfigurationFile {

    @Override
    public final void setDefault(final ConfigurationFile def){
        this.def = def;
    }

    //

    @Override
    public final void load(final String filename) throws IOException{
        load(new File(filename));
    }

    @Override
    public final void load(final File file) throws IOException{
        load(new FileReader(file));
    }

    @Override
    public final void load(final Reader reader) throws IOException{
        final YamlReader IN = new YamlReader(reader);
        config = (Map) IN.read();
        IN.close();
    }

    @Override
    public final void load(final InputStream stream) throws IOException{
        load(new InputStreamReader(stream));
    }

    @Override
    public final void loadFromString(final String yaml) throws IOException{
        final YamlReader IN = new YamlReader(yaml);
        config = (Map) IN.read();
        IN.close();
    }

    //

    @Override
    public final void save(final File file) throws IOException{
        final Map cfg = new HashMap(def.toMap());
        cfg.putAll(config);

        final YamlWriter OUT = new YamlWriter(new FileWriter(file));
        OUT.write(cfg);
        OUT.close();
    }

}
