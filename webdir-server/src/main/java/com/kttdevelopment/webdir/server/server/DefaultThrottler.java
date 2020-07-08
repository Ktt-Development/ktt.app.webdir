package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.sun.net.httpserver.HttpExchange;

public class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        try{
            return Integer.parseInt(Application.getPermissionsService().getPermissions().getOption(exchange.getRemoteAddress().getAddress(), "connection-limit").toString());
        }catch(final NumberFormatException ignored){
            return 0;
        }
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        return false;
    }

}
