package com.kttdevelopment.webdir.config;

import java.io.*;

public interface ConfigurationFile extends ConfigurationSection {

    void load(final String filename);

    void load(final File file);

    void load(final Reader reader);

    void load(final InputStream stream);

    void loadFromString(final String yaml);

    void save(final File file);

}
