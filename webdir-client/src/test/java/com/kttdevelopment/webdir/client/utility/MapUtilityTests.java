package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.*;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MapUtilityTests {

    @Test
    public final void testStringList(){
        final List list = new ArrayList(){{
            add(1);
            add(null);
            add('e');
        }};
        final List expected = new ArrayList(){{
            add("1");
            add(null);
            add("e");
        }};

        Assertions.assertEquals(expected, MapUtility.asStringList(list));
    }

    @Test
    public final void testStringMap(){
        final Map map = new HashMap(){{
            put(1, 0);
            put(null, null);
            put('e', "e");
        }};
        final Map expected = new HashMap(){{
            put("1", 0);
            put(null, null);
            put("e", "e");
        }};
        
        Assertions.assertEquals(expected, MapUtility.asStringObjectMap(map));
    }

    @Test
    public final void testStringObjectMap(){
        final Map map = new HashMap(){{
            put(1, 0);
            put(null, null);
            put('e', "e");
        }};
        final Map expected = new HashMap(){{
            put("1", 0);
            put(null, null);
            put("e", "e");
        }};

        Assertions.assertEquals(expected, MapUtility.asStringObjectMap(map));
    }

    @Test
    public final void testDeepCopy(){
        final List list = new ArrayList(){{
            add("test");
        }};
        final Map map = Map.of(
            "k", "v",
            "l", list,
            "m", Map.of(
                "k", "v",
                "l", list
            )
        );

        ((List) MapUtility.deepCopy(map).get("l")).set(0, "null");

        Assertions.assertEquals("test", list.get(0));
    }

}
