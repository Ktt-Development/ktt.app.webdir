package com.kttdevelopment.webdir.client.utility;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class YamlUtilityTests {

    @Test
    public void testString() throws IOException{
        Assertions.assertEquals("input", YamlUtility.asString(Yaml.createYamlInput("input: value").readYamlMapping().keys().stream().findFirst().get()));
        Assertions.assertEquals("value", YamlUtility.asString(Yaml.createYamlInput("- value").readYamlSequence().values().stream().findFirst().get()));
    }

    @Test
    public void testList() throws IOException{
        Assertions.assertEquals(List.of("input","value"), YamlUtility.asList(Yaml.createYamlInput("- input\n- value").readYamlSequence()));
    }

    @Test
    public void testMap() throws IOException{
        final YamlMapping map = Yaml.createYamlInput("input: value\nlist:\n  - input\n  - value\nmap:\n  input: value").readYamlMapping();
        Assertions.assertEquals(
            Map.of(
                "input", "value",
                "list", List.of("input","value"),
                "map", Map.of("input", "value")
            ), YamlUtility.asMap(map));
        Assertions.assertTrue(YamlUtility.containsKey("list", map));
        Assertions.assertFalse(YamlUtility.containsKey("list", null));
    }
}
