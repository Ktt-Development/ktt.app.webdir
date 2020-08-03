package server;

import com.kttdevelopment.webdir.generator.PluginLoader;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.*;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.List;
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
            new File(".root/renderTests/renderExactDuplicate.html"),
            "---\n" +
            "renderer:\n" +
            "  - plugin: DuplicateRenderTests\n" +
            "    renderer: first\n" +
            "---"
        ).forEach(TestFile::createTestFile);

        Main.main(null);

        final String url = "http://localhost:" + Vars.Test.port + "/renderTests";

        Assert.assertEquals("Renderers lower on the list are expected to render last", "second", TestResponse.getResponseContent(URI.create(url + "/renderOrder")));
        Assert.assertEquals("Renderers lower on the list are expected to render last","first",TestResponse.getResponseContent(URI.create(url + "/renderReverseOrder")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATE", TestResponse.getResponseContent(URI.create(url + "/renderExactDuplicate")));
    }

    @Test
    public void testExchangeRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".root/renderTestsEx/renderOrder.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n" +
            "  - secondEx\n" +
            "  - first\n" +
            "  - second\n" +
            "---",
            new File(".root/renderTestsEx/renderReverseOrder.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - secondEx\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n" +
            "  - exceptionEx\n" +
            "  - first\n" +
            "  - second\n" +
            "---",
            new File(".root/renderTestsEx/renderExactDuplicate.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeDuplicateRenderTests\n" +
            "    renderer: firstEx\n" +
            "---"
        ).forEach(TestFile::createTestFile);

        Main.main(null);

        final String url = "http://localhost:" + Vars.Test.port + "/renderTestsEx";

        Assert.assertEquals("Renderers lower on the list are expected to render last","secondEx",TestResponse.getResponseContent(URI.create(url + "/renderOrder")));
        Assert.assertEquals("Renderers lower on the list are expected to render last","firstEx",TestResponse.getResponseContent(URI.create(url + "/renderReverseOrder")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATEEX", TestResponse.getResponseContent(URI.create(url + "/renderExactDuplicate")));
    }

    //

    @Test
    public void testDefaultRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /defaultTests/index0.html\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /defaultTests/index0.html\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer: second",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/negative.html\n" +
            "    - \"!/defaultTests/negative.html\"\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/exact.txt\n" +
            "    - /defaultTests/*.cfg\n" +
            "    - /defaultTests/file.*\n" +
            "    - \"*.log\"\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n"
        ).forEach(TestFile::createTestFile);

        // test files
        List.of(
            new File(".root/defaultTests/exact.txt"),
            new File(".root/defaultTests/file.txt"),
            new File(".root/defaultTests/index0.html"),
            new File(".root/defaultTests/index1.html"),
            new File(".root/defaultTests/negative.html"),
            new File(".root/defaultTests/test.cfg"),
            new File(".root/defaultTests/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);

        final String url = "http://localhost:" + Vars.Test.port + "/defaultTests";

        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "first", TestResponse.getResponseContent(URI.create(url + "/index1")));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","first", TestResponse.getResponseContent(URI.create(url + "/index0")));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/exact.txt")));
        Assert.assertEquals("Default with *.cfg scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with file.* scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with *.log scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.log")));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", TestResponse.getResponseContent(URI.create(url + "/negative")));
    }

    @Test
    public void testDefaultExchangeRenderers() throws ExecutionException, InterruptedException{
                Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index0.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index1.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index0.html\n" +
            "     - /defaultTestsEx/index1.html\n" +
            "exchangeRenderer: secondEx",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTestsEx/negative.html\n" +
            "    - \"!/defaultTestsEx/negative.html\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTestsEx/exact.txt\n" +
            "    - /defaultTestsEx/*.cfg\n" +
            "    - /defaultTestsEx/file.*\n" +
            "    - \"*.log\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n"
        ).forEach(TestFile::createTestFile);

        // test files
        List.of(
            new File(".root/defaultTestsEx/exact.txt"),
            new File(".root/defaultTestsEx/file.txt"),
            new File(".root/defaultTestsEx/index0.html"),
            new File(".root/defaultTestsEx/index1.html"),
            new File(".root/defaultTestsEx/negative.html"),
            new File(".root/defaultTestsEx/test.cfg"),
            new File(".root/defaultTestsEx/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);

        final String url = "http://localhost:" + Vars.Test.port + "/defaultTestsEx";

        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "firstEx", TestResponse.getResponseContent(URI.create(url + "/index1")));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","firstEx", TestResponse.getResponseContent(URI.create(url + "/index0")));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/exact.txt")));
        Assert.assertEquals("Default with *.cfg scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with file.* scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with *.log scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.log")));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", TestResponse.getResponseContent(URI.create(url + "/negative")));
    }

    @Test @Ignore
    public void testFileRenderers(){
        // same above tests but with files (use absolute path for context url)
        // test raw render and def render
    }

}
