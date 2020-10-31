package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.kttdevelopment.webdir.client.*;
import com.sun.net.httpserver.HttpExchange;

import java.util.Objects;
import java.util.logging.Logger;

public final class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        final LocaleService locale = Main.getLocale();
        final Logger logger        = Main.getLogger(locale.getString("server.name"));

        final int limit = Integer.parseInt(Objects.requireNonNullElse(Main.getPermissions().getOption(exchange.getRemoteAddress().getAddress(), PermissionsService.CONNECTIONS), 0).toString());

        logger.finest(locale.getString("server.DefaultThrottler.limit",exchange.getRemoteAddress(), limit));
        return limit;
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        return getMaxConnections(exchange) == -1;
    }

}
