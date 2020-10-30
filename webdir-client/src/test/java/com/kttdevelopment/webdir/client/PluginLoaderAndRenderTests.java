package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        final File output = new File("_site");
        output.deleteOnExit();
        {
            for(final File file : Objects.requireNonNullElse(output.listFiles(), new File[0]))
                Files.delete(file.toPath());

            final File src = new File("_root");
            Assertions.assertTrue(src.mkdirs());
            src.deleteOnExit();
            
            final File render = new File(src, "render.html");
            render.deleteOnExit();
            Files.write(render.toPath(),"---\nrenderers:\n  - 1\nexchange_renderers:\n  - 2\n---".getBytes());

            final File v1 = new File(src, "v1.html");
            v1.deleteOnExit();
            Files.write(v1.toPath(),"---\nrenderers:\n  - plugin: ValidPlugin\n    renderer: 1\n---".getBytes());

            final File v2 = new File(src, "v2.html");
            v2.deleteOnExit();
            Files.write(v2.toPath(),"---\nrenderers:\n  - plugin: ValidDependent\n    renderer: 1\n---".getBytes());

            final File ro3 = new File(src, "ro3.html");
            ro3.deleteOnExit();
            Files.write(ro3.toPath(),"---\nrenderers:\n  - 2\n  -3\n---".getBytes());

            final File ro2 = new File(src, "ro2.html");
            ro2.deleteOnExit();
            Files.write(ro2.toPath(),"---\nrenderers:\n  - 3\n  -2\n---".getBytes());

            final File cp = new File(src, "cp.html");
            cp.deleteOnExit();
            Files.write(cp.toPath(),"---\nrenderers:\n  - 3\n  - copy\n---".getBytes());

            final File ex = new File(src, "ex.html");
            ex.deleteOnExit();
            Files.write(ex.toPath(),"---\nrenderers:\n  - ex\n---".getBytes());

            final File to = new File(src, "to.html");
            to.deleteOnExit();
            Files.write(to.toPath(),"---\nrenderers:\n  - times\n---".getBytes());

            final File cfg = new File(src, "cfg.html");
            cfg.deleteOnExit();
            Files.write(cfg.toPath(),"---\nrenderers:\n  - set\n  - get\n---".getBytes());

            final File out = new File(src, "out.html");
            out.deleteOnExit();
            Files.write(out.toPath(),"---\nrenderers:\n  - out\n---".getBytes());

            final File nl = new File(src, "null.html");
            nl.deleteOnExit();
            Files.write(nl.toPath(),"---\nrenderers:\n  - 'null'\n---".getBytes());

            Main.pageRenderingService = new PageRenderingService(
                new File(Main.getConfig().string(ConfigService.DEFAULT)),
                new File(Main.getConfig().string(ConfigService.SOURCES)),
                new File(Main.getConfig().string(ConfigService.OUTPUT))
            );

            // test render
            Assertions.assertNotEquals(0, new File(output, "render.html").length());
            // test exchange render ignored
            Assertions.assertNotEquals("2", Files.readString(new File(output, "render.html").toPath()));
            // test specific render
            Assertions.assertEquals("1", Files.readString(new File(output, "v1.html").toPath()));
            Assertions.assertEquals("2", Files.readString(new File(output, "v2.html").toPath()));
            // test render order
            Assertions.assertEquals("3", Files.readString(new File(output, "ro3.html").toPath()));
            Assertions.assertEquals("2", Files.readString(new File(output, "ro2.html").toPath()));
            // test render I/O
            Assertions.assertEquals("3", Files.readString(new File(output, "cp.html").toPath()));
            // test exception render
            Assertions.assertEquals("", Files.readString(new File(output, "ex.html").toPath()));
            // test timed out render
            Assertions.assertEquals("", Files.readString(new File(output, "to.html").toPath()));

            // test file render config transfer
            Assertions.assertEquals("1", Files.readString(new File(output, "cfg.html").toPath()));

            // test output change
            Assertions.assertTrue(new File(output, "output.html").exists());

            // null ignore
            Assertions.assertFalse(new File(output, "null.html").exists());
        }

        // config tests // todo
        {
            // test default
            // test default imports
            // test imports
            // test sub-imports
        }

        // server tests
        {

        }

        for(final File file : Objects.requireNonNull(output.listFiles()))
            Files.delete(file.toPath());
    }

}
