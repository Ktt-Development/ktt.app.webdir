package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import com.sun.net.httpserver.HttpExchange;

import java.util.Objects;

public final class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        return Integer.parseInt(Objects.requireNonNullElse(Main.getPermissions().getPermissions().getOption(exchange.getRemoteAddress().getAddress(), ServerVars.Permissions.connectionOption),ServerVars.Permissions.defaultConnectionOption).toString());
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        return getMaxConnections(exchange) == -1;
    }

}
