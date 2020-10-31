package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.PermissionsService;
import com.sun.net.httpserver.HttpExchange;

import java.util.Objects;

public final class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        return Integer.parseInt(Objects.requireNonNullElse(Main.getPermissions().getOption(exchange.getRemoteAddress().getAddress(), PermissionsService.CONNECTIONS), 0).toString());
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        return getMaxConnections(exchange) == -1;
    }

}
