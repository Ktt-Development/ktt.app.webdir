package com.kttdevelopment.webdir.liquid.tags.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public final class Server extends Tag {

    private final SimpleHttpServer server;

    public Server(final SimpleHttpServer server){
        super("api/server");
        this.server = server;
    }

    @Override
    public final Object render(final TemplateContext context, final LNode... nodes){
        final String param = asString(nodes[0].render(context)).toLowerCase();
        final String modifier = asString(nodes[1].render(context)).toLowerCase();

        switch(param){
            case "address":
                switch(modifier){
                    default:
                    case "address":
                        return server.getAddress().toString();
                    case "hostname":
                        return server.getAddress().getHostString();
                    case "port":
                        return server.getAddress().getPort();
                }
            case "context":
                switch(modifier){
                    default:
                    case "list":

                }
        }

        return null;
    }

}
