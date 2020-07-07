package com.kttdevelopment.webdir.api.serviceprovider;

public interface ConfigurationFile extends ConfigurationSection {

    void setDefault(final ConfigurationFile def);

    //

    boolean reload();

    boolean save();

}
