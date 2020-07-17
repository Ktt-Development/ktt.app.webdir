package com.kttdevelopment.webdir.generator.render;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class CircularImportChecker implements BiPredicate<List<String>,File> {


    private final List<File> checked = new ArrayList<>();

    @Override
    public final boolean test(final List<String> strings, final File source){
        return false;
    }



}
