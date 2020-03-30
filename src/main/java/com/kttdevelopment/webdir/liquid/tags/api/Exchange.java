package com.kttdevelopment.webdir.liquid.tags.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public final class Exchange extends Tag {

    private final SimpleHttpExchange exchange;

    public Exchange(final SimpleHttpExchange exchange){
        super("api/exchange");
        this.exchange = exchange;
    }

    @Override
    public final Object render(final TemplateContext context, final LNode... nodes){
        return null;
    }

}
