package com.kttdevelopment.webdir.parser;

import com.kttdevelopment.webdir.LinkedFile;

import java.util.LinkedList;

public class LinkedFileParser {

    private final LinkedList<ContentParser> parsers = new LinkedList<>();

    public LinkedFileParser(){ }

    public final void addContentParser(final ContentParser parser){
        parsers.add(parser);
    }

    public final String parse(final LinkedFile file){
        String str;

    }

}
