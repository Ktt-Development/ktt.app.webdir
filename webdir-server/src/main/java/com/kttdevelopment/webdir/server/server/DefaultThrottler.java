package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.handler.ServerExchangeThrottler;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import com.sun.net.httpserver.HttpExchange;

import java.util.Objects;
import java.util.logging.Logger;

public final class DefaultThrottler extends ServerExchangeThrottler {

    @Override
    public final int getMaxConnections(final HttpExchange exchange){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("server"));

        final int limit = Integer.parseInt(Objects.requireNonNullElse(Main.getPermissions().getPermissions().getOption(exchange.getRemoteAddress().getAddress(), ServerVars.Permissions.connectionOption),ServerVars.Permissions.defaultConnectionOption).toString());

        logger.finest(locale.getString("server.DefaultThrottler.limit",exchange.getRemoteAddress(), limit));
        return limit;
    }

    @Override
    public final boolean canIgnoreConnectionLimit(final HttpExchange exchange){
        return getMaxConnections(exchange) == -1;
    }

}
