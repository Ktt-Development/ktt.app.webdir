package com.kttdevelopment.webdir.api.serviceprovider;

import java.io.*;

public interface ConfigurationFile extends ConfigurationSection {

    void setDefault(final ConfigurationFile def);

    //

    void load(final String filename) throws IOException;

    void load(final File file) throws IOException;

    void load(final Reader reader) throws IOException;

    void load(final InputStream stream) throws IOException;

    void loadFromString(final String yaml) throws IOException;

    void save(final File file) throws IOException;

}
