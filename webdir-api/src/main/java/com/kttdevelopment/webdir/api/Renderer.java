package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public interface Renderer {

    String format(final File output, final ConfigurationSection yamlFrontMatter, final String content);

}
