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

package com.kttdevelopment.webdir.client.permissions;

import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public final class PermissionsUser {

    private final InetAddress user;
    private final List<String> groups = new ArrayList<>(), permissions = new ArrayList<>();
    private final Map<String,Object> options = new HashMap<>();

    public PermissionsUser(final String user, final Map<String,Object> value) throws UnknownHostException{
        this(InetAddress.getByName(user), value);
    }

    public PermissionsUser(final InetAddress user, final Map<String,Object> value){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.finest(locale.getString("permissions.permissionsUser.start", user.getHostAddress(), value));

        // uniform local address
        {
            // if user is loop back address (127.0.0.1) then use local address instead
            this.user = ExceptionUtility.requireNonExceptionElse(() -> user.isLoopbackAddress() ? InetAddress.getLocalHost() : user, user);
        }

        // null check
        {
            if(value == null){
                logger.severe(locale.getString("permissions.permissionsUser.null", user));
                return;
            }
        }

        // groups
        {
            final Object obj = value.get(PermissionsService.GROUPS);
            if(obj instanceof List<?>)
                groups.addAll(MapUtility.asStringList((List<?>) obj));
            else if(obj != null)
                groups.add(obj.toString());
        }

        // options
        {
            final Object obj = value.get(PermissionsService.OPTIONS);
            if(obj instanceof Map<?,?> && !((Map<?, ?>) obj).isEmpty())
                options.putAll(MapUtility.asStringObjectMap((Map<?,?>) obj));
        }

        // permissions
        {
            final Object obj = value.get(PermissionsService.PERMISSIONS);
            if(obj instanceof List<?>)
                permissions.addAll(MapUtility.asStringList((List<?>) obj));
        }

        logger.finest(locale.getString("permissions.permissionsUser.finish", this.user.getHostAddress()));
    }

    public final InetAddress getUser(){
        return user;
    }

    public final List<String> getGroups(){
        return Collections.unmodifiableList(groups);
    }

    public final Map<String,Object> getOptions(){
        return Collections.unmodifiableMap(options);
    }

    public final List<String> getPermissions(){
        return Collections.unmodifiableList(permissions);
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("user",user)
            .addObject("groups", groups)
            .addObject("options", options)
            .addObject("permissions", permissions)
            .toString();
    }

}
