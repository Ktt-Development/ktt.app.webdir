package com.kttdevelopment.webdir.server.renderer;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public interface PageRender extends Renderer {

    String format(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content);

}
