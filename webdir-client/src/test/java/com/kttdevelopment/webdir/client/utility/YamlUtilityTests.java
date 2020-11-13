package com.kttdevelopment.webdir.client.utility;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

public class YamlUtilityTests {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testString() throws IOException{
        Assertions.assertEquals("input", YamlUtility.asString(Yaml.createYamlInput("input: value").readYamlMapping().keys().stream().findFirst().get()));
        Assertions.assertEquals("value", YamlUtility.asString(Yaml.createYamlInput("- value").readYamlSequence().values().stream().findFirst().get()));
        Assertions.assertNull(YamlUtility.asString(Yaml.createYamlInput("nil: null").readYamlMapping().value("nil")));
        Assertions.assertNull(YamlUtility.asString(Yaml.createYamlInput("- null").readYamlSequence().values().stream().findFirst().get()));
    }

    @Test
    public void testList() throws IOException{
        Assertions.assertEquals(Arrays.asList("input", "value", null), YamlUtility.asList(Yaml.createYamlInput("- input\n- value\n- null").readYamlSequence()));
    }

    @Test
    public void testMap() throws IOException{
        final YamlMapping map = Yaml.createYamlInput("input: value\nlist:\n  - input\n  - value\n  - null\nmap:\n  input: value\n  nil: null").readYamlMapping();
        Assertions.assertEquals(
            Map.of(
                "input", "value",
                "list", Arrays.asList("input", "value", null),
                "map", new HashMap<String,String>(){{
                    put("input", "value");
                    put("nil", null);
                }}
            ), YamlUtility.asMap(map));
        Assertions.assertTrue(YamlUtility.containsKey("list", map));
        Assertions.assertFalse(YamlUtility.containsKey("list", null));
    }
}
