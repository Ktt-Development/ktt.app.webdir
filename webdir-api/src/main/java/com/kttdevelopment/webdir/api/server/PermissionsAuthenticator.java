package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.var.HttpCode;
import com.kttdevelopment.webdir.api.PluginService;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

public class PermissionsAuthenticator extends Authenticator {

    private final PluginService pluginService;
    private final String permission;

    public PermissionsAuthenticator(final PluginService pluginService, final String permission){
        this.pluginService = pluginService;
        this.permission = permission;
    }

    @Override
    public final Result authenticate(final HttpExchange exchange){
        return
            pluginService.hasPermission(exchange.getRemoteAddress().getAddress(),permission)
            ? new Authenticator.Success(exchange.getPrincipal())
            : new Authenticator.Failure(HttpCode.HTTP_Unauthorized);
    }

}
