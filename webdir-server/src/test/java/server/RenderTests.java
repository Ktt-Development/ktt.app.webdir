package server;

import com.kttdevelopment.webdir.generator.PluginLoader;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.*;

import java.io.File;
import java.net.URI;
import java.net.http.*;
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

    @Test @Ignore
    public void testRenderers(){
        Vars.Test.safemode = false;

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

        // test renderer content against server output
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

    //

    private String getResponseContent(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .build();

        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

}
