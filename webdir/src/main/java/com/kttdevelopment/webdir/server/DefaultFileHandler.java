package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.Application;
import com.kttdevelopment.webdir.api.handler.SimpleFileHandler;
import com.kttdevelopment.webdir.httpserver.SimpleHttpExchangeUnmodifiable;
import com.kttdevelopment.webdir.permissions.Permissions;
import com.kttdevelopment.webdir.pluginservice.PluginLibrary;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

// handles file reading
public class DefaultFileHandler extends FileHandler {

    public DefaultFileHandler(){
        super(new DefaultFileAdapter());
    }

    @Override
    public final void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final Permissions Permission = Application.getPermissionsService().getPermissions();

        final InetAddress address = exchange.getPublicAddress().getAddress();
        final PluginLibrary lib = Application.getPluginService().getLibrary();

        for(final SimpleFileHandler handler : lib.getHandlers()){
            if(Permission.hasPermission(address,lib.getHandlerPermission(handler)) && handler.test(exchange,source)){
                exchange.send(handler.handle(new SimpleHttpExchangeUnmodifiable(exchange),source,bytes));
                return; // only allow a single handler to process a file
            }
        }

        super.handle(exchange, source, bytes);
    }

}
