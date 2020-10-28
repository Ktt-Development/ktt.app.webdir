package com.kttdevelopment.webdir.client;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PluginLoaderTests {

    @BeforeAll
    public static void before(){
        new File("config.yml").deleteOnExit();
        Assertions.assertDoesNotThrow(() -> Main.logger = new LoggerService(), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.config = new ConfigService(new File("config.yml")), PermissionsServiceTests.class.getSimpleName() + " depends on ConfigService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.locale = new LocaleService("lang/locale"), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
    }

    @AfterAll
    public static void cleanup(){
        LoggerServiceTests.clearLogFiles();
    }

    @Test
    public void test() throws IOException{
        // filter tests
        {
            final File notjar = new File("../_plugins/notjar");
            Assertions.assertTrue(notjar.exists() || notjar.createNewFile());
            Assertions.assertEquals(15, Objects.requireNonNullElse(new File("../_plugins").listFiles(), new File[0]).length, "Test plugins are not yet loaded. Please run > mvn package");
            final PluginLoader loader = new PluginLoader(new File("../_plugins"));
            Assertions.assertEquals(2, loader.getPlugins().size(), "Some invalid plugins were loaded");
        }
        // render + plugin service tests (make sure to change above valid plugin count).

        // run assertions here, not in plugin
    }

}
