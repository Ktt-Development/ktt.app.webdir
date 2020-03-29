package com.kttdevelopment.webdir.liquid.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public class Include_Relative extends Tag {

    public Include_Relative(){
        super("include_relative");
    }

    @Override
    public Object render(final TemplateContext context, final LNode... nodes){
        return null;
    }

}
