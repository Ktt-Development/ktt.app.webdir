package com.kttdevelopment.webdir.client.utility;

import java.util.*;

public abstract class MapUtility {

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
