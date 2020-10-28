package com.kttdevelopment.webdir.client.utility;

import com.amihaiemil.eoyaml.Yaml;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class YamlUtilityTests {

    @Test
    public void test() throws IOException{
        Assertions.assertEquals("input", YamlUtility.asString(Yaml.createYamlInput("input: value").readYamlMapping().keys().stream().findFirst().get()));
        Assertions.assertEquals("value", YamlUtility.asString(Yaml.createYamlInput("- value").readYamlSequence().values().stream().findFirst().get()));
    }
}
