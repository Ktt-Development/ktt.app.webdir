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

package com.kttdevelopment.webdir.client.utility;

import java.util.*;

public final class ToStringBuilder {

    private final String className;
    private final Map<String,Object> map = new LinkedHashMap<>();

    public ToStringBuilder(final String className){
        this.className = className;
    }

    public ToStringBuilder(final String className, final Map<String,Object> map){
        this(className);
        map.forEach(this.map::put);
    }

    public final ToStringBuilder addObject(final String key, final Object value){
        map.put(key,value);
        return this;
    }

    //

    @Override
    public final String toString(){
        final StringBuilder OUT = new StringBuilder();
        OUT.append(className).append('{');
        map.forEach((s, o) -> OUT.append(s).append('=').append(asString(o)).append(", "));
        if(OUT.toString().endsWith(", "))
            OUT.delete(OUT.length()-2,OUT.length());
        OUT.append('}');
        return OUT.toString();
    }

    private String asString(final Object object){
        if(object == null)
            return null;
        else if(object instanceof String)
            return '\'' + object.toString() + '\'';
        else if(object instanceof Object[])
            return Arrays.toString((Object[]) object);
        else
            return object.toString();
    }

}
