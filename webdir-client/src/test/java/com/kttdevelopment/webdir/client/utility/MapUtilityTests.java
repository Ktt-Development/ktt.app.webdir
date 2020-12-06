package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.*;

import java.util.*;

public class MapUtilityTests {

    // todo
    @Test @Disabled
    public final void testStringList(){

    }

    @Test @Disabled
    public final void testStringMap(){

    }

    @Test @Disabled
    public final void testStringObjectMap(){

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
