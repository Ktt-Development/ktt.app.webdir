package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PluginLoaderAndRenderTests {

    @BeforeAll
    public static void before(){
        new File("config.yml").deleteOnExit();
        Assertions.assertDoesNotThrow(() -> Main.logger = new LoggerService(), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.config = new ConfigService(new File("config.yml")), PermissionsServiceTests.class.getSimpleName() + " depends on ConfigService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.locale = new LocaleService("lang/locale"), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
    }

    @AfterAll
    public static void cleanup() throws IOException{
        LoggerServiceTests.clearLogFiles();

        for(final File file : Objects.requireNonNull(new File("_site/defaultsTests").listFiles()))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNull(new File("_site").listFiles()))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNull(new File("_root/defaultsTests").listFiles()))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNull(new File("_root").listFiles()))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNull(new File("_default").listFiles()))
            Files.delete(file.toPath());
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
        final File src = new File("_root");
            Assertions.assertTrue(src.mkdirs());
            src.deleteOnExit();
        final File output = new File("_site");
            output.deleteOnExit();
        { // page renderer dependencies (src + def)
            final File render = new File(src, "render.html");
            render.deleteOnExit();
            Files.write(render.toPath(), "---\nrenderers:\n  - 1\nexchange_renderers:\n  - 2\n---".getBytes());

            final File v1 = new File(src, "v1.html");
            v1.deleteOnExit();
            Files.write(v1.toPath(), "---\nrenderers:\n  - plugin: ValidPlugin\n    renderer: 1\n---".getBytes());

            final File v2 = new File(src, "v2.html");
            v2.deleteOnExit();
            Files.write(v2.toPath(), "---\nrenderers:\n  - plugin: ValidDependent\n    renderer: 1\n---".getBytes());

            final File ro3 = new File(src, "ro3.html");
            ro3.deleteOnExit();
            Files.write(ro3.toPath(), "---\nrenderers:\n  - 2\n  -3\n---".getBytes());

            final File ro2 = new File(src, "ro2.html");
            ro2.deleteOnExit();
            Files.write(ro2.toPath(), "---\nrenderers:\n  - 3\n  -2\n---".getBytes());

            final File cp = new File(src, "cp.html");
            cp.deleteOnExit();
            Files.write(cp.toPath(), "---\nrenderers:\n  - 3\n  - copy\n---".getBytes());

            final File ex = new File(src, "ex.html");
            ex.deleteOnExit();
            Files.write(ex.toPath(), "---\nrenderers:\n  - ex\n---".getBytes());

            final File to = new File(src, "to.html");
            to.deleteOnExit();
            Files.write(to.toPath(), "---\nrenderers:\n  - times\n---".getBytes());

            final File cfg = new File(src, "cfg.html");
            cfg.deleteOnExit();
            Files.write(cfg.toPath(), "---\nrenderers:\n  - set\n  - get\n---".getBytes());

            final File out = new File(src, "out.html");
            out.deleteOnExit();
            Files.write(out.toPath(), "---\nrenderers:\n  - out\n---".getBytes());

            final File nl = new File(src, "null.html");
            nl.deleteOnExit();
            Files.write(nl.toPath(), "---\nrenderers:\n  - 'null'\n---".getBytes());

            // default dependencies
            final File defaults = new File("_default");
            Assertions.assertTrue(defaults.mkdirs());
            defaults.deleteOnExit();

            Map.of(
                new File(defaults, "index.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/index.html\n" +
                "renderers: 2",
                new File(defaults, "index1.yml"),
                "default:\n" +
                "  index: 1\n" +
                "  scope:\n" +
                "    - /defaultsTests/index1.html\n" +
                "renderers: 2",
                new File(defaults, "index-1.yml"),
                "default:\n" +
                "  index: -1\n" +
                "  scope:\n" +
                "    - /defaultsTests/index.html\n" +
                "    - /defaultsTests/index1.html\n" +
                "renderers: second",
                new File(defaults, "negative.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/negative.html\n" +
                "    - \"!/defaultsTests/negative.html\"\n" +
                "renderers: 2",
                new File(defaults, "scope.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/exact.txt\n" +
                "    - /defaultsTests/*.cfg\n" +
                "    - /defaultsTests/file.*\n" +
                "    - \"*.log\"\n" +
                "renderers: 2"
            ).forEach((f, v) -> Assertions.assertDoesNotThrow(() -> Files.write(f.toPath(), v.getBytes())));

            final File defaultsInput = new File(src, "defaultsTests");
            Assertions.assertTrue(defaultsInput.mkdirs());
            defaultsInput.deleteOnExit();

            List.of(
                new File(defaultsInput, "exact.txt"),
                new File(defaultsInput, "file.txt"),
                new File(defaultsInput, "index.html"),
                new File(defaultsInput, "index1.html"),
                new File(defaultsInput, "negative.html"),
                new File(defaultsInput, "test.cfg"),
                new File(defaultsInput, "test.log")
            ).forEach(file -> {
                Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(file.createNewFile()));
            });

            // config dependencies (port must not be 80).

            // permissions dependencies

            // server render dependencies
        }

        Main.pageRenderingService = new PageRenderingService(
            new File(Main.getConfig().string(ConfigService.DEFAULT)),
            new File(Main.getConfig().string(ConfigService.SOURCES)),
            new File(Main.getConfig().string(ConfigService.OUTPUT))
        );

        // test render
        {
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

        // config tests
        {
            // test files
            final File defaultOutput = new File(output, "defaultsTests");
            defaultOutput.deleteOnExit();
    
            // test index and no index
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "index1.html").toPath()), "Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)");
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "index.html").toPath()), "Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)");
    
            // test scope
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "exact.txt").toPath()), "Default with exact scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "test.cfg").toPath()), "Default with *.cfg scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "test.cfg").toPath()), "Default with file.* scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultOutput, "test.log").toPath()), "Default with *.log scope should render file");
    
            // test negative scope
            Assertions.assertEquals("", Files.readString(new File(defaultOutput, "negative.html").toPath()), "Default with negation ! scope should not render file");
        }

        if(true) return; // TODO ↓

        // server tests
        {
            final String head = "http://localhost:" + 8080;
            // above config and render tests but with http

            Assertions.assertNotEquals("2", getResponseContent(head + "/render"));
            Assertions.assertEquals("1", getResponseContent(head + "/v1"));
            Assertions.assertEquals("2", getResponseContent(head + "/v2"));
            Assertions.assertEquals("3", getResponseContent(head + "/ro3"));
            Assertions.assertEquals("2", getResponseContent(head + "/ro2"));
            Assertions.assertEquals("3", getResponseContent(head + "/cp"));
            Assertions.assertNull(getResponseContent(head + "/ex"));
            Assertions.assertNull(getResponseContent(head + "/to"));
            Assertions.assertEquals("1", getResponseContent(head + "/cfg"));
            Assertions.assertNotNull(getResponseContent(head + "/output"));
            Assertions.assertNull(getResponseContent(head + "/null"));

            // exchange render tests

            // render permissions tests

                // test required
                // test not required
                // test negative

            // conn limit tests

                // test unset
                // test set
                // test -1
        }
    }

    private String getResponseContent(final String url){
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .build();

        try{
            return HttpClient
                .newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .get();
        }catch(final InterruptedException | ExecutionException ignored){
            return null;
        }
    }

}
