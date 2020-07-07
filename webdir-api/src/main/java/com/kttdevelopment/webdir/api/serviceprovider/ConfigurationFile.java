package com.kttdevelopment.webdir.api.serviceprovider;

public interface ConfigurationFile extends ConfigurationSection {

    void setDefault(final ConfigurationSection def);

    //

    boolean reload();

    boolean save();

}
