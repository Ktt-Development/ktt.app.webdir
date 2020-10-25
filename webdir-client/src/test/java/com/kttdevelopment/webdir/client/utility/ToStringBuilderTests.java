package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ToStringBuilderTests {

    @Test
    public final void testConstructor(){
        final String className = getClass().getSimpleName();
        Assertions.assertEquals(className + "{}", new ToStringBuilder(className).toString());
    }

    @Test
    public final void testConstructorWithParams(){
        final String v1 = "variable1", v2 = "variable2";
        final Object o1 = 1, o2 = 2.0;

        final Map<String,Object> vars = new LinkedHashMap<>(){{
            put(v1,o1);
            put(v2,o2);
        }};

        final String className = getClass().getSimpleName();

        Assertions.assertEquals(className + String.format("{%s=%s, %s=%s}",v1,o1,v2,o2),new ToStringBuilder(className,vars).toString());
    }

    @Test
    public final void testAddObject(){
        final String v1 = "variable1", v2 = "variable2";
        final Object o1 = 1, o2 = 2.0;

        final String className = getClass().getSimpleName();

        Assertions.assertEquals(className + String.format("{%s=%s}",v1,o1),new ToStringBuilder(className).addObject(v1,o1).toString());
        Assertions.assertEquals(className + String.format("{%s=%s, %s=%s}",v1,o1,v2,o2),new ToStringBuilder(className).addObject(v1,o1).addObject(v2,o2).toString());
    }

    @Test
    public final void testValueOf(){
        final String v1 = "asString", v2 = "asArray";
        final Object o1 = "string", o2 = new String[]{"string1, string2"};

        final String className = getClass().getSimpleName();

        Assertions.assertEquals(className + String.format("{%s=\"%s\", %s=%s}", v1, o1, v2, Arrays.toString((Object[]) o2)), new ToStringBuilder(className).addObject(v1, o1).addObject(v2, o2).toString());
    }

}
