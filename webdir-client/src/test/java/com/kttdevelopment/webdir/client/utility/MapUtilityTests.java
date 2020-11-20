package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class MapUtilityTests {

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
