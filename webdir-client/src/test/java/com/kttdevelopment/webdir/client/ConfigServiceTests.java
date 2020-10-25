package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigServiceTests {

    @TempDir
    File configTest;

    @Test
    public void testConfig() throws IOException{
        Main.logger = new LoggerService();

        final File config = new File(configTest, UUID.randomUUID().toString() + ".yml");
        final AtomicReference<ConfigService> service = new AtomicReference<>();

        Assertions.assertDoesNotThrow( () -> service.set(new ConfigService(config)));

        // test file exists and is valid
        Assertions.assertTrue(config.exists());
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(config).readYamlMapping());

        // test OK
        Assertions.assertDoesNotThrow( () -> service.set(new ConfigService(config)));
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(config).readYamlMapping());

        // test malformed
        Files.delete(config.toPath());
        Files.write(config.toPath(),"::".getBytes());
        Assertions.assertDoesNotThrow( () -> service.set(new ConfigService(config)));
        Assertions.assertTrue(config.exists());
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(config).readYamlMapping());

        // test missing keys & config override default
        Files.delete(config.toPath());
        Files.write(config.toPath(),"server: true".getBytes());
        Assertions.assertDoesNotThrow( () -> service.set(new ConfigService(config)));
        Assertions.assertTrue(config.exists());

        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(config).readYamlMapping());

        Assertions.assertEquals("true", service.get().getConfiguration().string("server"));
        Assertions.assertEquals("80", service.get().getConfiguration().string("port"));

        LoggerServiceTests.clearLogFiles();
    }

}
