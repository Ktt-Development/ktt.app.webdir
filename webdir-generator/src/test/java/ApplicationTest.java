import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.PluginLoader;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class ApplicationTest {

    @Before
    public void before(){
        Main.testMode = false;
    }

    @Test
    public void testPluginLoading(){
        Main.testSafeMode = false;
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
        Main.testSafeMode = true;
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
        Main.testSafeMode = false;
        Main.main(null);

        Assert.assertEquals("order.html has second listed as the final renderer but result was incorrect (returns first render or exception)","second", Files.readString(new File("_site/order.html").toPath()));
        Assert.assertEquals("order-reverse.html has first listed as the final renderer but result was incorrect","first", Files.readString(new File("_site/order-reverse.html").toPath()));
        Assert.assertEquals("exact-first.html specifically calls for first renderer but returned duplicate","first", Files.readString(new File("_site/exact-first.html").toPath()));
        Assert.assertEquals("exact-duplicate.html specifically calls for duplicate renderer but returned first","DUPLICATE", Files.readString(new File("_site/exact-duplicate.html").toPath()));
    }

    @Test
    public void testClear() throws IOException{
        Main.testSafeMode = false;

        final File testRoot = new File(".root/test.html");
        final File testOutput = new File("_site/test.html");

        if(!testRoot.exists())
            Files.createFile(testRoot.toPath());
        Main.main(null);
        Assert.assertTrue("Generator did not copy file from root folder",testOutput.exists());

        Files.delete(testRoot.toPath());
        Main.testClear = true;
        Main.main(null);
        Assert.assertFalse("Generator did not remove file that was no longer present in root folder",testOutput.exists());
    }

    @Test
    public void testImpl(){
        Main.testSafeMode = false;
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
        Assert.assertEquals("Plugin logger name should be plugin name",pluginYml.getPluginName(),plugin.getLogger().getName());

    }

}
