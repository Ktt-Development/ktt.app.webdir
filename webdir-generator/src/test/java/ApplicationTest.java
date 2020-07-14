import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.PluginLoader;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import org.junit.*;

import java.util.logging.Logger;

public class ApplicationTest {

    /* to test:

        Test case on plugin impl!

        Test renders
        - page render order
        - page render duplicate
        - page render skip exception
        - page render 'clean' config

        plugin-render-tests.jar {
            first, second, exception
        }

        plugin-render-duplicate.jar {
            first -> DUPLICATE
        |

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

}
