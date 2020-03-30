package com.kttdevelopment.webdir.liquid.tags;

import com.kttdevelopment.webdir.main.Main;
import com.kttdevelopment.webdir.parser.FileConfigPair;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

import java.io.File;
import java.util.Objects;

public final class Include extends Tag {

    public Include(){
        super("include");
    }

    @Override
    public final Object render(final TemplateContext context, final LNode... nodes){
        final String       source = asString(nodes[0].render(context));
        final FileConfigPair file = new FileConfigPair(new File(Main.root + source));
        return Objects.requireNonNullElse(file.readFile(),"");
    }

}
