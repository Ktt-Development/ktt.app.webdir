package com.kttdevelopment.webdir.parser;

import java.util.Map;

public abstract class ContentParser {

    public abstract String parse(final String content, final Map config);

}
