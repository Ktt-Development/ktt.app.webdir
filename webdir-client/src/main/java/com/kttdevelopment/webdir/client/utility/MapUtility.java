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

public abstract class MapUtility {

    public static List<String> asStringList(final List<?> list){
        if(list == null || list.isEmpty()) return new ArrayList<>();
        final List<String> OUT = new ArrayList<>();
        for(final Object o : list)
            OUT.add(o == null ? null : o.toString());
        return OUT;
    }

    public static Map<String,String> asStringMap(final Map<?,?> map){
        if(map == null || map.isEmpty()) return new HashMap<>();
        final Map<String,String> OUT = new HashMap<>();
        Object key, obj;
        for(final Map.Entry<?,?> entry : map.entrySet())
            OUT.put((key = entry.getKey()) == null ? null : key.toString(), (obj = entry.getValue()) == null ? null : obj.toString());
        return OUT;
    }

    public static Map<String,Object> asStringObjectMap(final Map<?,?> map){
        if(map == null || map.isEmpty()) return new HashMap<>();
        final Map<String,Object> OUT = new HashMap<>();
        Object key;
        for(final Map.Entry<?,?> entry : map.entrySet())
            OUT.put((key = entry.getKey()) == null ? null : key.toString(), entry.getValue());
        return OUT;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map deepCopy(final Map map){
        final Map copy = new HashMap();
        map.forEach((k, v) -> {
            if(v instanceof List)
                copy.put(k, new ArrayList((List) v));
            else if(v instanceof Map)
                copy.put(k, deepCopy((Map) v));
            else
                copy.put(k, v);
        });
        return copy;
    }

}
