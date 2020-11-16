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
        new File("permissions.yml").deleteOnExit();
        new File("1.yml").deleteOnExit();
        new File("2.yml").deleteOnExit();
        new File("r.yml").deleteOnExit();
        new File("c1.yml").deleteOnExit();
        new File("c2.yml").deleteOnExit();
        new File("_plugins/ValidPlugin").deleteOnExit();

        Assertions.assertTrue(_defaults.exists() || _defaults.mkdirs());
        _defaults.deleteOnExit();

        Assertions.assertTrue(_root.exists() || _root.mkdirs());
        _root.deleteOnExit();
        Assertions.assertTrue(defaultsInput.exists() || defaultsInput.mkdirs());
        defaultsInput.deleteOnExit();

        _site.deleteOnExit();
        defaultsOutput.deleteOnExit();
    }

    @AfterAll
    public static void cleanup() throws IOException{
        LoggerServiceTests.clearLogFiles();

        for(final File file : Objects.requireNonNullElse(defaultsOutput.listFiles(), new File[0]))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNullElse(_site.listFiles(), new File[0]))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNullElse(defaultsInput.listFiles(), new File[0]))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNullElse(_root.listFiles(), new File[0]))
            Files.delete(file.toPath());
        for(final File file : Objects.requireNonNull(_defaults.listFiles()))
            Files.delete(file.toPath());

        Main.getServer().getServer().stop();
    }

    private static final File _defaults      = new File("_default");
    private static final File _site          = new File("_site");
    private static final File _root          = new File("_root");
    private static final File defaultsInput  = new File(_root, "defaultsTests");
    private static final File defaultsOutput = new File(_site, defaultsInput.getName());

    @Test
    public void test() throws IOException{
        // plugin deps
        {
            final File notjar = new File("_plugins/notjar");
            Assertions.assertTrue(notjar.exists() || notjar.createNewFile());
        }
        // render deps
        {
            writeInput("render", "---\nrenderers:\n  - 1\nexchange_renderers:\n  - 2\n---");
            writeInput("v1"    , "---\nrenderers:\n  - plugin: ValidPlugin\n    renderer: 1\n---");
            writeInput("v2"    , "---\nrenderers:\n  - plugin: ValidDependent\n    renderer: 1\n---");
            writeInput("ro3"   , "---\nrenderers:\n  - 2\n  -3\n---");
            writeInput("ro2"   , "---\nrenderers:\n  - 3\n  -2\n---");
            writeInput("cp"    , "---\nrenderers:\n  - 3\n  - copy\n---");
            writeInput("ex"    , "---\nrenderers:\n  - ex\n---");
            writeInput("to"    , "---\nrenderers:\n  - times\n---");
            writeInput("cfg"   , "---\nrenderers:\n  - set\n  - get\n---");
            writeInput("out"   , "---\nrenderers:\n  - out\n---");
            writeInput("null"  , "---\nrenderers:\n  - 'null'\n---");
            writeInput("nullCfg"  , "---\nignore: true\n---");
            writeInput("false"  , "---\nrenderers:\n  - false\n---\nF");
            writeInput("exchange"  , "---\nrenderers:\n  - 1\n---\nF");
            writeInput("perm"  , "---\nrenderers:\n  - 1\n---\nF");
            writeInput("tf"  , "");
            writeInput("ff"  , "");

            // default dependencies
            Map.of(
                new File(_defaults, "index.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/index.html\n" +
                "renderers: 2",
                new File(_defaults, "index1.yml"),
                "default:\n" +
                "  index: 1\n" +
                "  scope:\n" +
                "    - /defaultsTests/index1.html\n" +
                "renderers: 2",
                new File(_defaults, "index-1.yml"),
                "default:\n" +
                "  index: -1\n" +
                "  scope:\n" +
                "    - /defaultsTests/index.html\n" +
                "    - /defaultsTests/index1.html\n" +
                "renderers: second",
                new File(_defaults, "negative.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/negative.html\n" +
                "    - \"!/defaultsTests/negative.html\"\n" +
                "renderers: 2",
                new File(_defaults, "scope.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /defaultsTests/exact.txt\n" +
                "    - /defaultsTests/*.cfg\n" +
                "    - /defaultsTests/file.*\n" +
                "    - \"*.log\"\n" +
                "renderers: 2",
                new File(_defaults, "exchange.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /exchange.html\n" +
                "exchange_renderers: exchange",
                new File(_defaults, "perm.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - /perm.html\n" +
                "exchange_renderers:\n" +
                "  - perm\n" +
                "  - perm2",
                new File(_defaults, "readme.yml"),
                "default:\n" +
                "  scope:\n" +
                "    - '*:/*'\n" +
                "    - '!*.gitignore'\n" +
                "exchange_renderers: 2",
                new File(_defaults, "tf.yml"),
                "default:\n" +
                "  scope:\n" +
                "    -\n" +
                "      file: true\n" +
                "    - 'tf.html'\n" +
                "renderers: 2",
                new File(_defaults, "ff.yml"),
                "default:\n" +
                "  scope:\n" +
                "    -\n" +
                "      file: false\n" +
                "    - 'ff.html'\n" +
                "renderers: 2"
            ).forEach((f, v) -> Assertions.assertDoesNotThrow(() -> Files.write(f.toPath(), v.getBytes())));

            List.of(
                new File(defaultsInput, "exact.txt"),
                new File(defaultsInput, "file.txt"),
                new File(defaultsInput, "index.html"),
                new File(defaultsInput, "index1.html"),
                new File(defaultsInput, "negative.html"),
                new File(defaultsInput, "test.cfg"),
                new File(defaultsInput, "test.log")
            ).forEach(file -> Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(file.createNewFile())));

            // imports

            Files.write(new File("1.yml").toPath(), "renderers:\n  - plugin: ValidPlugin\n    renderer: 1".getBytes());
            Files.write(new File("2.yml").toPath(), "import: 1\nrenderers: 2".getBytes());
            Files.write(new File("_root/3.yml").toPath(), "renderers: 3".getBytes());
            Files.write(new File("r.yml").toPath(), "import_relative: _root/3.yml".getBytes());
            Files.write(new File("c1.yml").toPath(), "import: c2.yml\nrenderers: 2".getBytes());
            Files.write(new File("c2.yml").toPath(), "import: c1.yml\nrenderers: 1".getBytes());
            writeInput("import1", "---\nimport: 1\n---");
            writeInput("importO", "---\nimport: 2\n---");
            writeInput("importR", "---\nimport_relative: 3\n---");
            writeInput("importR2", "---\nimport: r.yml\n---");
            writeInput("importC", "---\nimport: c1.yml\n---");

            // config dependencies (port must not be 80).
            Files.write(new File("config.yml").toPath(), "port: 8080\nserver: true".getBytes());

            // permissions dependencies
            Files.write(new File("permissions.yml").toPath(), "users:\n  127.0.0.1:\n    permissions:\n      - perm\n      - !perm2\n    options:\n      connection-limit: -1".getBytes());
        }

        Main.main(null);

        // plugin tests
        {
            Assertions.assertEquals(15, Objects.requireNonNullElse(new File("_plugins").listFiles(File::isFile), new File[0]).length, "Test plugins are not yet loaded. Please run > mvn package");
            Assertions.assertEquals(2, Main.getPluginLoader().getPlugins().size(), "Some invalid plugins were loaded");
        }

        // plugin service tests
        {
            final WebDirPlugin plugin = Objects.requireNonNull(Main.getPluginLoader().getPlugin("ValidPlugin"));
            Assertions.assertEquals("ValidPlugin", plugin.getPluginName());
            Assertions.assertEquals(plugin, plugin.getPlugin("ValidPlugin"));
            Assertions.assertEquals(new File(Main.getPluginLoader().getPluginsFolder(), "ValidPlugin").getAbsoluteFile(), plugin.getPluginFolder().getAbsoluteFile());
            Assertions.assertEquals("ValidPlugin", plugin.getPluginYml().get(PluginLoader.NAME));
            Assertions.assertEquals(Main.getConfig().string(ConfigService.DEFAULT), plugin.getConfigYml().get(ConfigService.DEFAULT));

            Assertions.assertEquals("ValidPlugin", plugin.getLogger().getName());
            Assertions.assertEquals(Main.getPluginLoader().getPluginsFolder().getAbsoluteFile(), plugin.getPluginsFolder().getAbsoluteFile());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.DEFAULT)), plugin.getDefaultsFolder());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.SOURCES)), plugin.getSourcesFolder());
            Assertions.assertEquals(new File(Main.getConfig().string(ConfigService.OUTPUT)), plugin.getOutputFolder());
        }

        // test render
        {
            // test render
            Assertions.assertNotEquals(0, new File(_site, "render.html").length());
            // test exchange render ignored
            Assertions.assertNotEquals("2", Files.readString(new File(_site, "render.html").toPath()));
            // test specific render
            Assertions.assertEquals("1", Files.readString(new File(_site, "v1.html").toPath()));
            Assertions.assertEquals("2", Files.readString(new File(_site, "v2.html").toPath()));
            // test render order
            Assertions.assertEquals("3", Files.readString(new File(_site, "ro3.html").toPath()));
            Assertions.assertEquals("2", Files.readString(new File(_site, "ro2.html").toPath()));
            // test render I/O
            Assertions.assertEquals("3", Files.readString(new File(_site, "cp.html").toPath()));
            // test exception render
            Assertions.assertEquals("", Files.readString(new File(_site, "ex.html").toPath()));
            // test timed out render
            Assertions.assertEquals("", Files.readString(new File(_site, "to.html").toPath()));

            // test file render config transfer
            Assertions.assertEquals("1", Files.readString(new File(_site, "cfg.html").toPath()));

            // test output change
            Assertions.assertTrue(new File(_site, "output.html").exists());

            // null ignore
            Assertions.assertFalse(new File(_site, "null.html").exists());
            Assertions.assertFalse(new File(_site, "nullCfg.html").exists());

            // filter
            Assertions.assertEquals("F", Files.readString(new File(_site, "false.html").toPath()));

            Assertions.assertEquals("1", Files.readString(new File(_site, "exchange.html").toPath()));

            // file: true/false (#72)
            Assertions.assertEquals("2", Files.readString(new File(_site, "tf.html").toPath()));
            Assertions.assertNotEquals("2", Files.readString(new File(_site, "ff.html").toPath()));
        }

        // config tests
        {
            // test index and no index
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "index1.html").toPath()), "Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)");
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "index.html").toPath()), "Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)");
    
            // test scope
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "exact.txt").toPath()), "Default with exact scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "test.cfg").toPath()), "Default with *.cfg scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "test.cfg").toPath()), "Default with file.* scope should render file");
            Assertions.assertEquals("2", Files.readString(new File(defaultsOutput, "test.log").toPath()), "Default with *.log scope should render file");
    
            // test negative scope
            Assertions.assertEquals("", Files.readString(new File(defaultsOutput, "negative.html").toPath()), "Default with negation ! scope should not render file");
        }

        // import tests
        {
            Assertions.assertEquals("1", Files.readString(new File(_site, "import1.html").toPath()));
            Assertions.assertEquals("2", Files.readString(new File(_site, "importO.html").toPath()));
            Assertions.assertEquals("3", Files.readString(new File(_site, "importR.html").toPath()));
            Assertions.assertEquals("3", Files.readString(new File(_site, "importR2.html").toPath()));
            Assertions.assertNotEquals("", Files.readString(new File(_site, "importC.html").toPath()));
        }

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
            Assertions.assertEquals("", getResponseContent(head + "/ex"));
            Assertions.assertEquals("", getResponseContent(head + "/to"));
            Assertions.assertEquals("1", getResponseContent(head + "/cfg"));
            Assertions.assertNotNull(getResponseContent(head + "/output"));
            Assertions.assertNull(getResponseContent(head + "/null"));
            Assertions.assertEquals("F", getResponseContent(head + "/false"));

            // test index.html -> / (#65)
            Assertions.assertEquals("2", getResponseContent(head + '/' + defaultsInput.getName()));

            // exchange render tests
            Assertions.assertEquals("exchange", getResponseContent(head + "/exchange"));

            // render permissions tests
            Assertions.assertEquals("perm", getResponseContent(head + "/perm"));

            // test drives
            final String ignore = Files.readString(new File("../.gitignore").toPath());
            final String path = head + "/files/" + new File("../.gitignore").getCanonicalPath().replace('\\', '/');
            Assertions.assertEquals(ignore, getResponseContent(path), String.format("Failed to read %s (make sure tests are run with Windows OS)", path));

            // test drive default
            final String readmePath = head + "/files/" + new File("../README.md").getCanonicalPath().replace('\\', '/');
            Assertions.assertEquals("2", getResponseContent(readmePath), String.format("Failed to read default with %s (make sure tests are run with Windows OS)", readmePath));

            // test raw (#75)
            final String readme = Files.readString(new File("../README.md").toPath());
            final String raw = head + "/raw/" + new File("../README.md").getCanonicalPath().replace('\\', '/');
            Assertions.assertEquals(readme, getResponseContent(raw), String.format("Failed to read default with %s (make sure tests are run with Windows OS)", raw));

            // test folder default
            final String folder = head + "/files/" + new File("../").getCanonicalPath().replace('\\', '/');
            Assertions.assertEquals("2", getResponseContent(folder), String.format("Failed to read default with %s (make sure tests are run with Windows OS)", readmePath));
        }
    }

    private void writeInput(final String fileName, final String content){
        final File render = new File(_root, fileName + ".html");
        Assertions.assertDoesNotThrow(() -> Files.write(render.toPath(), content.getBytes()));
    }

    private String getResponseContent(final String url){
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.replace('\\', '/')))
            .timeout(Duration.ofSeconds(1000))
            .build();

        try{
            return HttpClient
                .newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .get();
        }catch(final InterruptedException | ExecutionException e){
            return null;
        }
    }

}
