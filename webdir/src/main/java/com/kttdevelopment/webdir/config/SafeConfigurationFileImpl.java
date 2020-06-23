package com.kttdevelopment.webdir.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;

import java.io.*;
import java.util.Map;

public final class SafeConfigurationFileImpl extends ConfigurationFileImpl {

    public SafeConfigurationFileImpl(){
        super();
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
