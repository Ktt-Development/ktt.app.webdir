package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

// todo
@Deprecated
public interface ExchangeRenderer extends Renderer {

    // maybe change source to path?
    String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content);

    default String getPermission(){ return null; }

}
