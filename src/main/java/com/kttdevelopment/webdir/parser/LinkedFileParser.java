package com.kttdevelopment.webdir.parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LinkedFileParser {

    private final LinkedList<ContentParser> parsers = new LinkedList<>();

    public LinkedFileParser(){ }

    public final void addContentParser(final ContentParser parser){
        parsers.add(parser);
    }

    public final String parse(final FileConfigPair file){
        AtomicReference<String> content = new AtomicReference<>(file.readFile());
        final Map       config  = Objects.requireNonNullElse(file.readConfig(),new HashMap());

        parsers.forEach(new Consumer<ContentParser>() {
            @Override
            public void accept(final ContentParser contentParser){
                content.set(contentParser.parse(content.get(),config));
            }
        });
    }

}
