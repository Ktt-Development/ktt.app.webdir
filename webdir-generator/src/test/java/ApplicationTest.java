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
import java.util.concurrent.ExecutionException;

public class ApplicationTest {

    @Before
    public void before(){
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
            Assert.assertNull("Server should not have loaded plugin " + badPlugin,pluginLoader.getPlugin(badPlugin));

        for(final String goodPlugin : goodPlugins)
            Assert.assertNotNull("Server should have loaded plugin " + goodPlugin,pluginLoader.getPlugin(goodPlugin));

        Assert.assertEquals("Server should have only loaded " + goodPlugins.length + " plugins",goodPlugins.length,pluginLoader.getPlugins().size());

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
        Main.main(null);

        Assert.assertEquals("order.html has second listed as the final renderer but result was incorrect (returns first render or exception)","second", Files.readString(new File("_site/order.html").toPath()));
        Assert.assertEquals("order-reverse.html has first listed as the final renderer but result was incorrect","first", Files.readString(new File("_site/order-reverse.html").toPath()));
        Assert.assertEquals("exact-first.html specifically calls for first renderer but returned duplicate","first", Files.readString(new File("_site/exact-first.html").toPath()));
        Assert.assertEquals("exact-duplicate.html specifically calls for duplicate renderer but returned first","DUPLICATE", Files.readString(new File("_site/exact-duplicate.html").toPath()));
    }

    /*
        default {
            index int
            scope []
        }
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testDefaultRenderer() throws IOException{
        Vars.Test.safemode = false;
        Vars.Test.server = false;

        // test files
        final File def = new File(".root/default");
        if(!def.exists() && !def.mkdirs())
            Assert.fail("Failed to create test directory default");
        List.of(
            new File(".root/default/exact.txt"),
            new File(".root/default/file.txt"),
            new File(".root/default/index.html"),
            new File(".root/default/index1.html"),
            new File(".root/default/negative.html"),
            new File(".root/default/test.cfg"),
            new File(".root/default/test.log")
        ).forEach(file -> {
            try{
                file.createNewFile();
                file.deleteOnExit();
            }catch(final IOException e){
                e.printStackTrace();
                Assert.fail("Failed to create test file " + file);
            }
        });

        Main.main(null);

        // test index and no index
        Assert.assertEquals("Default with index 1 should override default with index -1","first", Files.readString(new File("_site/default/index1.html").toPath()));
        Assert.assertEquals("Default with no index (0) should override default with index -1","first", Files.readString(new File("_site/default/index.html").toPath()));

        // test scope
        Assert.assertEquals("File in default exact scope should use default","first", Files.readString(new File("_site/default/exact.txt").toPath()));
        Assert.assertEquals("Default with *.cfg should accept test config for default","first", Files.readString(new File("_site/default/test.cfg").toPath()));
        Assert.assertEquals("Default with file.* should accept test file for default","first", Files.readString(new File("_site/default/test.cfg").toPath()));
        Assert.assertEquals("Default with *.log should accept test log for default","first", Files.readString(new File("_site/default/test.log").toPath()));

        // test negative scope
        Assert.assertEquals("Default with negation ! should not use default","", Files.readString(new File("_site/default/negative.html").toPath()));
    }

    @Test
    public void testClear() throws IOException{
        Vars.Test.safemode = true;
        Vars.Test.server = false;

        final File testRoot = new File(".root/test.html");
        final File testOutput = new File("_site/test.html");

        if(!testRoot.exists())
            Files.createFile(testRoot.toPath());
        Main.main(null);
        Assert.assertTrue("Generator did not copy file from root folder",testOutput.exists());

        Files.delete(testRoot.toPath());
        Vars.Test.clear = true;
        Main.main(null);
        Assert.assertFalse("Generator did not remove file that was no longer present in root folder",testOutput.exists());
    }

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
                "Referencing a deleted page should null content",
                getResponseContent(URI.create(String.format(url, port, System.currentTimeMillis())))
            );
        }catch(final Exception e){
            if(!e.getMessage().contains("header parser received no bytes"))
                Assert.fail("Referencing a non-existent page should return no content");
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
