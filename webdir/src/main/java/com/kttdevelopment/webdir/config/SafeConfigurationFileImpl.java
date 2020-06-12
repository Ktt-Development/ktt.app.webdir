package com.kttdevelopment.webdir.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;

public final class SafeConfigurationFileImpl extends ConfigurationFileImpl {

    public SafeConfigurationFileImpl(){
    }

    public SafeConfigurationFileImpl(final File configFile, final boolean skipRead){
        super(configFile, skipRead);
    }

    public SafeConfigurationFileImpl(final File configFile) throws FileNotFoundException, YamlException{
        super(configFile);
    }

    public SafeConfigurationFileImpl(final Reader reader) throws IOException{
        super(reader);
    }

    public SafeConfigurationFileImpl(final InputStream stream) throws IOException{
        super(stream);
    }

    public SafeConfigurationFileImpl(final String yaml) throws YamlException{
        super(yaml);
    }

    //


    @Override
    public synchronized final void setDefault(final ConfigurationFile def){
        try{ super.setDefault(def);
        }catch(final Exception ignored){ }
    }

    @Override
    public synchronized final void reload(){
        try{ super.reload();
        }catch(final Exception ignored){ }
    }

    @Override
    public synchronized final void save(){
        try{ super.save();
        }catch(final Exception ignored){ }
    }

    @Override
    public synchronized final void saveDefault(){
        try{ super.saveDefault();
        }catch(final Exception ignored){ }
    }

}
