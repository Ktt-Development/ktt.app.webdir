package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.var.HttpCode;
import com.kttdevelopment.webdir.api.PluginService;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

/**
 * Limits a handler to only users who have a required permission.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public class PermissionsAuthenticator extends Authenticator {

    private final PluginService pluginService;
    private final String permission;

    /**
     * Creates a permissions authenticator.
     *
     * @param pluginService plugin service
     * @param permission required permission
     *
     * @since 01.00.00
     * @author Ktt Development
     */
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
