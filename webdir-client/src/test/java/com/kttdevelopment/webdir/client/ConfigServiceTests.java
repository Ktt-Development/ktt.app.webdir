package com.kttdevelopment.webdir.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

public class ConfigServiceTests {

    @TempDir
    public final File configTest = new File(UUID.randomUUID().toString());

    // assignment irrelevant, class runs required methods
    @SuppressWarnings("UnusedAssignment")
    @Test
    public void testConfig() throws IOException{
        Assertions.assertDoesNotThrow(() -> Main.logger = new LoggerService(), getClass().getSimpleName() + " depends on LoggerService for tests.");

        final File config = new File(configTest, UUID.randomUUID().toString() + ".yml");

        ConfigService service = new ConfigService(config);

        // test file exists and is valid
        Assertions.assertTrue(config.exists());

        // test OK
        service = new ConfigService(config);

        // test malformed
        Files.delete(config.toPath());
        Files.write(config.toPath(),"X: {".getBytes());
        service = new ConfigService(config);
        Assertions.assertTrue(config.exists());

        // test missing keys & config override default
        Files.write(config.toPath(),"server: true".getBytes());
        service = new ConfigService(config);
        Assertions.assertTrue(config.exists());

        Assertions.assertEquals("true", service.getConfiguration().get(ConfigService.SERVER).toString());
        Assertions.assertEquals("80", service.getConfiguration().get(ConfigService.PORT).toString());

        LoggerServiceTests.clearLogFiles();
    }

}
