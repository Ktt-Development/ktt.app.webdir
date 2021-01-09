/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
