package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.sun.net.httpserver.HttpExchange;

public class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        // todo: add permissions option conn limit
        return -1;
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        // todo: add permissions option conn limit test
        return false;
    }

}
