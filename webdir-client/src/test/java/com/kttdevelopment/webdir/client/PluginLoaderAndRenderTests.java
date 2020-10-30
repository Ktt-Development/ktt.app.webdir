package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PluginLoaderAndRenderTests {

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
            final File notjar = new File("_plugins/notjar");
            Assertions.assertTrue(notjar.exists() || notjar.createNewFile());
            Assertions.assertEquals(15, Objects.requireNonNullElse(new File("_plugins").listFiles(File::isFile), new File[0]).length, "Test plugins are not yet loaded. Please run > mvn package");
            Main.pluginLoader = new PluginLoader(new File("_plugins"));
            Assertions.assertEquals(2, Main.getPluginLoader().getPlugins().size(), "Some invalid plugins were loaded");
        }
        // plugin service tests
        {
            final WebDirPlugin plugin = Objects.requireNonNull(Main.getPluginLoader().getPlugin("ValidPlugin"));
            Assertions.assertEquals("ValidPlugin", plugin.getPluginName());
            Assertions.assertEquals(plugin, plugin.getPlugin("ValidPlugin"));
            Assertions.assertEquals(Main.getPluginLoader().getPluginsFolder().getAbsoluteFile(), plugin.getPluginsFolder().getAbsoluteFile());
            Assertions.assertEquals(new File(Main.getPluginLoader().getPluginsFolder(), "ValidPlugin").getAbsoluteFile(), plugin.getPluginFolder().getAbsoluteFile());
            Assertions.assertEquals("ValidPlugin", plugin.getLogger().getName());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.DEFAULT)), plugin.getDefaultsFolder());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.SOURCES)), plugin.getSourcesFolder());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.OUTPUT)), plugin.getOutputFolder());
        }
        // render tests
        if(true) return;
        { // todo
            Main.pageRenderingService = new PageRenderingService(
                new File(Main.getConfig().string(ConfigService.DEFAULT)),
                new File(Main.getConfig().string(ConfigService.SOURCES)),
                new File(Main.getConfig().string(ConfigService.OUTPUT))
            );

            // test render
            // test exchange render ignored
            // test specific render
            // test exception render
            // test timed out render

            // test file render transfer // todo
            // test output change // todo
        }

        // config tests // todo
        {
            // test default
            // test default imports
            // test imports
            // test sub-imports
        }
    }

}
