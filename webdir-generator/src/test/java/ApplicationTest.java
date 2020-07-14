import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.PluginLoader;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ApplicationTest {

    /* to test:

        Test case on plugin impl!

        Test renders
        - page render 'clean' config

        plugin-render-tests.jar {
            first, second, exception
        }

        plugin-render-duplicate.jar {
            first -> DUPLICATE
        }

     */

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
            "circle-dep1",
            "circle-dep2",
            "Const-Exception",
            "Missing-Dep",
            "No-Extends",
            "No-Main",
            "Exception-Plugin",
            "Timed-Plugin"
        };

        final String[] goodPlugins = {
            "dependency",
            "dependent",
            "Render-Plugin",
            "Valid-Plugin",
            "Duplicate-Plugin"
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
    public void testRender(){

    }

}
