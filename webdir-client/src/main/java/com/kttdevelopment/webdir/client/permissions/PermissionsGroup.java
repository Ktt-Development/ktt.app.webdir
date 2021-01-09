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

import java.util.*;
import java.util.logging.Logger;

public final class PermissionsGroup {

    private final String group;
    private final List<String> inheritance = new ArrayList<>(), permissions = new ArrayList<>();
    private final Map<String,Object> options = new HashMap<>();

    public PermissionsGroup(final String group, final Map<String,Object> value){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.finest(locale.getString("permissions.permissionsGroup.start", group, value));

        this.group = group;

        // null check
        {
            if(value == null){
                logger.severe(locale.getString("permissions.permissionsGroup.null", group));
                return;
            }
        }

        // inheritance
        {
            final Object obj = value.get(PermissionsService.INHERITANCE);
            if(obj instanceof List<?>)
                inheritance.addAll(MapUtility.asStringList((List<?>) obj));
            else if(obj != null && !(obj instanceof Map<?,?>))
                inheritance.add(obj.toString());
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

        logger.finest(locale.getString("permissions.permissionsGroup.finish", group));
    }

    public final String getGroup(){
        return group;
    }

    public final List<String> getInheritance(){
        return Collections.unmodifiableList(inheritance);
    }

    public final List<String> getPermissions(){
        return Collections.unmodifiableList(permissions);
    }

    public final Map<String,Object> getOptions(){
        return Collections.unmodifiableMap(options);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("group", getGroup())
            .addObject("inheritance", getInheritance())
            .addObject("options", getOptions())
            .addObject("permissions", getPermissions())
            .toString();
    }

}
