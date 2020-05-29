package com.kttdevelopment.webdir.api.formatter;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;

public interface Formatter {

    String format(final SimpleHttpExchange exchange, final File source, final YamlFrontMatter yaml, final String content);

}
