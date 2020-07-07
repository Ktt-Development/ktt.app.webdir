package com.kttdevelopment.webdir.server.formatter;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

public abstract class YamlFrontMatter {

    public abstract boolean hasFrontMatter();

    public abstract ConfigurationSection getFrontMatter();

    public abstract String getFrontMatterAsString();

    public abstract String getContent();

}
