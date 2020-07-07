package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

public abstract class PluginYml {

    public abstract String getPluginName();

    public abstract String getPluginVersion();

    public abstract String getAuthor();

    public abstract String[] getAuthors();

    public abstract ConfigurationSection getConfiguration();

}
