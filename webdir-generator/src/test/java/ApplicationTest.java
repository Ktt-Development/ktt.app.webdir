import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.generator.*;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ApplicationTest {

    @BeforeClass
    public static void before(){
        Vars.Test.testmode = false;
    }

    @Test
    public void testPluginLoading(){
        Vars.Test.safemode = false;
        Vars.Test.server = false;
        Main.main(null);

        final String[] badPlugins = {
            /*
                - plugin missing yml
                - plugin missing name
                - plugin malformed yml
             */
            "CircularDependency1",
            "CircularDependency2",
            "ConstructorException",
            "MissingDependency",
            "MissingMain",
            "NoExtends",
            "NoMain",
            "ThrowsException",
            "TimedOut"
        };

        final String[] goodPlugins = {
            "Dependency",
            "Dependent",
            "DuplicateRenderTests",
            "RenderTests",
            "Valid"
        };

        final PluginLoader pluginLoader = Main.getPluginLoader();

        for(final String badPlugin : badPlugins)
            Assert.assertNull("Server should not have loaded plugin: " + badPlugin,pluginLoader.getPlugin(badPlugin));

        for(final String goodPlugin : goodPlugins)
            Assert.assertNotNull("Server should have loaded plugin: " + goodPlugin,pluginLoader.getPlugin(goodPlugin));

        Assert.assertEquals("Server should have only loaded: " + goodPlugins.length + " plugins",goodPlugins.length,pluginLoader.getPlugins().size());
    }

    @Test
    public void testSafeMode(){
        Vars.Test.safemode = true;
        Vars.Test.server = false;
        Main.main(null);
        Assert.assertTrue("Safe-mode should not load any plugins",Main.getPluginLoader().getPlugins().isEmpty());
    }

    /*
        plugin-render-tests.jar {
            first, second, exception
        }

        plugin-render-duplicate.jar {
            first -> DUPLICATE
        }
     */

    @Test
    public void testRenderer() throws IOException{
        Vars.Test.safemode = false;
        Vars.Test.server = false;

        Map.of(
            new File(".root/renderTests/renderOrder.html"),
            "---\n" +
            "renderer:\n" +
            "  - first\n" +
            "  - second\n" +
            "  - exception\n" +
            "---",
            new File(".root/renderTests/renderReverseOrder.html"),
            "---\n" +
            "renderer:\n" +
            "  - second\n" +
            "  - first\n" +
            "---",
            new File(".root/renderTests/renderExactFirst.html"),
            "---\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "---",
            new File(".root/renderTests/renderExactDuplicate.html"),
            "---\n" +
            "renderer:\n" +
            "  - plugin: DuplicateRenderTests\n" +
            "    renderer: first\n" +
            "---"
        ).forEach(TestFile::createTestFile);

        Main.main(null);

        Assert.assertEquals("Renders lower on the list are expected to run last (thus overriding any content)","second", Files.readString(new File("_site/renderTests/renderOrder.html").toPath()));
        Assert.assertEquals("Renders lower on the list are expected to run last (thus overriding any content)","first", Files.readString(new File("_site/renderTests/renderReverseOrder.html").toPath()));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","first", Files.readString(new File("_site/renderTests/renderExactFirst.html").toPath()));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATE", Files.readString(new File("_site/renderTests/renderExactDuplicate.html").toPath()));
    }

    /*
        default {
            index int
            scope []
        }
     */
    @Test
    public void testDefaultRenderer() throws IOException{
        Vars.Test.safemode = false;
        Vars.Test.server = false;

        Map.of(
            new File(".default/index.yml"), 
            "default:\n" +
            "  scope:\n" +
            "     - /defaultTests/index.html\n" +
            "renderer: first",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer: first",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /defaultTests/index.html\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer: second",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/negative.html\n" +
            "    - \"!/defaultTests/negative.html\"\n" +
            "renderer: first",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/exact.txt\n" +
            "    - /defaultTests/*.cfg\n" +
            "    - /defaultTests/file.*\n" +
            "    - \"*.log\"\n" +
            "renderer: first"
        ).forEach(TestFile::createTestFile);
        
        // test files
        List.of(
            new File(".root/defaultTests/exact.txt"),
            new File(".root/defaultTests/file.txt"),
            new File(".root/defaultTests/index.html"),
            new File(".root/defaultTests/index1.html"),
            new File(".root/defaultTests/negative.html"),
            new File(".root/defaultTests/test.cfg"),
            new File(".root/defaultTests/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);

        // test index and no index
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)","first", Files.readString(new File("_site/defaultTests/index1.html").toPath()));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","first", Files.readString(new File("_site/defaultTests/index.html").toPath()));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","first", Files.readString(new File("_site/defaultTests/exact.txt").toPath()));
        Assert.assertEquals("Default with *.cfg scope should render file","first", Files.readString(new File("_site/defaultTests/test.cfg").toPath()));
        Assert.assertEquals("Default with file.* scope should render file","first", Files.readString(new File("_site/defaultTests/test.cfg").toPath()));
        Assert.assertEquals("Default with *.log scope should render file","first", Files.readString(new File("_site/defaultTests/test.log").toPath()));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", Files.readString(new File("_site/defaultTests/negative.html").toPath()));
    }

    @Test
    public void testClear() throws IOException{
        Vars.Test.safemode = true;
        Vars.Test.server = false;

        final File testRoot = new File(".root/test.html");
        final File testOutput = new File("_site/test.html");

        if(!testRoot.getParentFile().exists() && !testRoot.getParentFile().mkdirs())
            Assert.fail("Failed to create test root directory");
        if(!testRoot.exists() && !testRoot.createNewFile())
            Assert.fail("Failed to create test file");
        Main.main(null);
        Assert.assertTrue("Generator did not copy file from root folder",testOutput.exists());

        if(!testRoot.delete())
            Assert.fail("Failed to delete test file from root");
        Vars.Test.clear = true;
        Main.main(null);
        Assert.assertFalse("Generator did not remove file that was no longer present in root folder",testOutput.exists());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testImpl(){
        Vars.Test.safemode = false;
        Vars.Test.server = false;
        Main.main(null);

        final WebDirPlugin plugin = Main.getPluginLoader().getPlugin("Valid");

        Assert.assertNotNull("Valid plugin should be loaded by the server",plugin);

        final PluginYml pluginYml = plugin.getPluginYml();
        Assert.assertEquals("Plugin yml should return correct plugin name","Valid",pluginYml.getPluginName());
        Assert.assertEquals("Plugin dependencies in yml should return no dependencies",0,pluginYml.getDependencies().length);
        Assert.assertEquals("Plugin yml should return correct plugin version","v1",pluginYml.getPluginVersion());
        Assert.assertEquals("First plugin author should be first on list","first",pluginYml.getAuthor());
        Assert.assertEquals("Second plugin author should be last on list","second",pluginYml.getAuthors()[1]);

        Assert.assertEquals("Plugin should be able to get other loaded plugins on the server",plugin,plugin.getPlugin("Valid"));
        Assert.assertEquals("Plugin folder should be plugins folder + plugin name",new File(".plugins/Valid"),plugin.getPluginFolder());
        Assert.assertTrue("Using getPluginFolder should create a folder if it does not exist",new File(".plugins/Valid").exists());
        Assert.assertEquals("Plugin logger name should be plugin name",pluginYml.getPluginName(),plugin.getLogger().getName());

        new File(".plugins/Valid").delete();
    }
    
    @Test
    public void testServer() throws ExecutionException, InterruptedException, IOException{
        Vars.Test.safemode = true;
        Vars.Test.server = true;
        Main.main(null);

        final String url = "http://localhost:%s/%s";
        final int port = 8080;
        final String target = String.valueOf(System.currentTimeMillis());
        final Path targetFile = new File(".root/" + target + ".html").toPath();

        // test none
        try{
            Assert.assertNull(
                "Referencing a non-existent page should return null content",
                getResponseContent(URI.create(String.format(url, port, target)))
            );
        }catch(final Exception e){
            if(!e.getMessage().contains("header parser received no bytes"))
                Assert.fail("Referencing a non-existent page should return no content");
        }

        // test add
        final String value = String.valueOf(System.currentTimeMillis());
        Files.write(targetFile,value.getBytes());

        Assert.assertEquals(
            "Server should be able to retrieve newly added file",
            value,
            getResponseContent(URI.create(String.format(url,port,target)))
        );

        // test mod
        final String newValue = String.valueOf(System.currentTimeMillis());
        Files.write(targetFile,newValue.getBytes());

        Assert.assertEquals(
            "Server should be able to retrieve modified file",
            newValue,
            getResponseContent(URI.create(String.format(url,port,target)))
        );

        // test del
        Files.delete(targetFile);
        try{
            Assert.assertNull(
                "Referencing a deleted page should return null content",
                getResponseContent(URI.create(String.format(url, port, System.currentTimeMillis())))
            );
        }catch(final Exception e){
            if(!e.getMessage().contains("header parser received no bytes"))
                Assert.fail("Referencing a deleted page should return null content");
        }
    }

    private String getResponseContent(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .build();

        return HttpClient.newHttpClient().sendAsync(request,HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

}
