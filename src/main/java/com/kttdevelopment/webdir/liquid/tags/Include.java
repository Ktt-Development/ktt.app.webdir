package com.kttdevelopment.webdir.liquid.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public class Include extends Tag {

    public Include(){
        super("include");
    }

    @Override
    public final Object render(final TemplateContext context, final LNode... nodes){
        return null;
    }

}
