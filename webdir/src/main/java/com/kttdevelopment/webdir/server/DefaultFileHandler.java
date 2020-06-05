package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.handler.HandlerEntry;
import com.kttdevelopment.webdir.httpserver.SimpleHttpExchangeUnmodifiable;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.pluginservice.PluginHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class DefaultFileHandler extends FileHandler {

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final Permissions Permission = Application.permissions.getPermissions();

        final InetAddress address = exchange.getPublicAddress().getAddress();
        final List<PluginHandler> handlers = Application.pluginService.getHandlers();

        for(final PluginHandler handler : handlers){
            final HandlerEntry entry = handler.getEntry();
            if(Permission.hasPermission(address,entry.getPermission()) && entry.getCondition().test(exchange,source)){
                exchange.send(entry.getHandler().handle(new SimpleHttpExchangeUnmodifiable(exchange),source,bytes));
                return;
            }
        }

        super.handle(exchange, source, bytes);
    }

}
