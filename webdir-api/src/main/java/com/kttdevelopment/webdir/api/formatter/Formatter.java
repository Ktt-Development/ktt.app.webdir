package com.kttdevelopment.webdir.api.formatter;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public interface Formatter {

    String format(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content);

}
