package com.kttdevelopment.webdir.client;

import org.junit.jupiter.api.*;

import java.io.File;

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

    /**
     * https://github.com/Ktt-Development/webdir/tree/79e8dae/webdir-test-plugins
     * Invalid tests:
     * - not jar     * - circular dependencies
     * - constructor exception
     * - missing dep
     * - missing yml
     * - missing main
     * - no main extends
     * - no plugin name
     * - no yml file
     * - constructor exception
     * - timed out
     */

}
