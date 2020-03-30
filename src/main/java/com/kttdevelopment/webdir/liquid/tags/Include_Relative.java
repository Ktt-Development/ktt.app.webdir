package com.kttdevelopment.webdir.liquid.tags;

import com.kttdevelopment.webdir.parser.FileConfigPair;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

import java.io.File;
import java.util.Objects;

public final class Include_Relative extends Tag {

    private final File targetFile;

    public Include_Relative(final File targetFile){
        super("include_relative");
        this.targetFile = targetFile;
    }

    @Override
    public final Object render(final TemplateContext context, final LNode... nodes){
        final String       source = asString(nodes[0].render(context));
        final FileConfigPair file = new FileConfigPair(new File(targetFile.getAbsolutePath() + '\\' + source));
        return Objects.requireNonNullElse(file.readFile(), "");
    }

}
