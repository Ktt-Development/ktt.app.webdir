package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public interface ExchangeRenderer extends Renderer {

    String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content);

    default String getPermission(){ return null; }

}
