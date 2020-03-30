package com.kttdevelopment.webdir.parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class LinkedFileParser {

    private final LinkedList<ContentParser> parsers = new LinkedList<>();

    public LinkedFileParser(){ }

    public final void addContentParser(final ContentParser parser){
        parsers.add(parser);
    }

    public final String parse(final FileConfigPair file){
        final AtomicReference<String> content = new AtomicReference<>(file.readFile());
        final Map config  = Objects.requireNonNullElse(file.readConfig(),new HashMap());

        parsers.forEach(contentParser -> content.set(contentParser.parse(content.get(), config)));
        return content.get();
    }

}
