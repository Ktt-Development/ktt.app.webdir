package server;

import com.kttdevelopment.webdir.generator.PluginLoader;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.*;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RenderTests {

    @Test
    public void testExtendedPluginLoading(){
        Vars.Test.safemode = false;

        final String[] goodPlugins = {
            "RenderTests",
            "DuplicateRenderTests",
            "ExchangeRenderTests",
            "ExchangeDuplicateRenderTests",
            "FileHandlerRenderTests",
            "FileHandlerDuplicateRenderTests"
        };

        Main.main(null);

        final PluginLoader pluginLoader = Vars.Main.getPluginLoader();

        for(final String goodPlugin : goodPlugins)
            Assert.assertNotNull("Server should have loaded plugin: " + goodPlugin,pluginLoader.getPlugin(goodPlugin));

        Assert.assertEquals("Server should have only loaded: " + goodPlugins.length + " plugins",goodPlugins.length,pluginLoader.getPlugins().size());
    }

    //

    @Test
    public void testRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".root/renderTests/renderOrder.html"),
            "---\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "  - second\n" +
            "  - exception\n" +
            "  - firstEx\n" +
            "  - secondEx\n" +
            "---",
            new File(".root/renderTests/renderReverseOrder.html"),
            "---\n" +
            "renderer:\n" +
            "  - second\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "  - firstEx\n" +
            "  - secondEx\n" +
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

        final String url = "http://localhost:" + Vars.Test.port + "/renderTests";

        Assert.assertEquals("Renderers lower on the list are expected to render last","second",TestResponse.getResponseContent(URI.create(url + "/renderOrder")));
        Assert.assertEquals("Renderers lower on the list are expected to render last","first",TestResponse.getResponseContent(URI.create(url + "/renderReverseOrder")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer", "first", TestResponse.getResponseContent(URI.create(url + "/renderExactFirst")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATE", TestResponse.getResponseContent(URI.create(url + "/renderExactDuplicate")));
    }

    @Test @Ignore
    public void testExchangeRenderers(){
        // same above tests but with exchange renderers
    }

    //

    @Test @Ignore
    public void testDefaultRenderers(){
        // test that classic defaults work and that default file handler works (below)
    }

    @Test @Ignore
    public void testFileRenderers(){
        // same above tests but with files (use absolute path for context url)
        // test raw render and def render
    }

}
