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
