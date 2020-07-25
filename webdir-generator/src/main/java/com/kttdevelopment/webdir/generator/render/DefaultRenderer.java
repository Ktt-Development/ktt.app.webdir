package com.kttdevelopment.webdir.generator.render;

import java.io.File;
import java.util.function.BiFunction;

public class DefaultRenderer implements BiFunction<File,String,String> {

    @Override // todo: render logic
    public final String apply(final File file, final String content){
        return content;
    }

}
