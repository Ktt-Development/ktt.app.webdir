package server;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.generator.PluginLoader;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import org.junit.Assert;
import org.junit.Test;
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

        final String url = "http://localhost:" + Vars.Test.getTestPort() + "/renderTests";

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

        final String url = "http://localhost:" + Vars.Test.getTestPort() + "/renderTestsEx";

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

        final String url = "http://localhost:" + Vars.Test.getTestPort() + "/defaultTests";

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

        final String url = "http://localhost:" + Vars.Test.getTestPort() + "/defaultTestsEx";

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

    @Test
    public void testFileRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /C:/*/fileTests/index0.html\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTests/index1.html\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTests/index0.html\n" +
            "     - /C:/*/fileTests/index1.html\n" +
            "renderer: second",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTests/negative.html\n" +
            "    - \"!/C:/*/fileTests/negative.html\"\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTests/exact.txt\n" +
            "    - /C:/*/fileTests/*.cfg\n" +
            "    - /C:/*/fileTests/file.*\n" +
            "    - \"*.log\"\n" +
            "renderer:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n"
        ).forEach(TestFile::createTestFile);

        // test files
        List.of(
            new File(".test/fileTests/exact.txt"),
            new File(".test/fileTests/file.txt"),
            new File(".test/fileTests/index0.html"),
            new File(".test/fileTests/index1.html"),
            new File(".test/fileTests/negative.html"),
            new File(".test/fileTests/test.cfg"),
            new File(".test/fileTests/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);
        Thread.sleep(1000);

        final String url = "http://localhost:" + Vars.Test.getTestPort() + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTests").getAbsolutePath());

        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "first", TestResponse.getResponseContent(URI.create(url + "/index1.html")));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","first", TestResponse.getResponseContent(URI.create(url + "/index0.html")));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/exact.txt")));
        Assert.assertEquals("Default with *.cfg scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with file.* scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with *.log scope should render file","first", TestResponse.getResponseContent(URI.create(url + "/test.log")));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", TestResponse.getResponseContent(URI.create(url + "/negative.html")));
    }

    @Test
    public void testFileRenderersEx() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsEx/index0.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsEx/index1.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsEx/index0.html\n" +
            "     - /C:/*/fileTestsEx/index1.html\n" +
            "exchangeRenderer: secondEx",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsEx/negative.html\n" +
            "    - \"!/C:/*/fileTestsEx/negative.html\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsEx/exact.txt\n" +
            "    - /C:/*/fileTestsEx/*.cfg\n" +
            "    - /C:/*/fileTestsEx/file.*\n" +
            "    - \"*.log\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n"
        ).forEach(TestFile::createTestFile);

        // test files
        final List<File> testFiles = List.of(
            new File(".test/fileTestsEx/exact.txt"),
            new File(".test/fileTestsEx/file.txt"),
            new File(".test/fileTestsEx/index0.html"),
            new File(".test/fileTestsEx/index1.html"),
            new File(".test/fileTestsEx/negative.html"),
            new File(".test/fileTestsEx/test.cfg"),
            new File(".test/fileTestsEx/test.log")
        );
        testFiles.forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);
        Thread.sleep(1000);

        final String url = "http://localhost:" + Vars.Test.getTestPort() + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTestsEx").getAbsolutePath());

        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "firstEx", TestResponse.getResponseContent(URI.create(url + "/index1.html")));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","firstEx", TestResponse.getResponseContent(URI.create(url + "/index0.html")));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/exact.txt")));
        Assert.assertEquals("Default with *.cfg scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with file.* scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with *.log scope should render file","firstEx", TestResponse.getResponseContent(URI.create(url + "/test.log")));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", TestResponse.getResponseContent(URI.create(url + "/negative.html")));
    }

    @Test
    public void testFileRenderersFH() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = false;
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index0.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index1.html\n" +
            "exchangeRenderer:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index0.html\n" +
            "     - /C:/*/fileTestsFH/index1.html\n" +
            "exchangeRenderer: secondFH",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsFH/negative.html\n" +
            "    - \"!/C:/*/fileTestsFH/negative.html\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsFH/exact.txt\n" +
            "    - /C:/*/fileTestsFH/*.cfg\n" +
            "    - /C:/*/fileTestsFH/file.*\n" +
            "    - \"*.log\"\n" +
            "exchangeRenderer:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n"
        ).forEach(TestFile::createTestFile);

        // test files
        List.of(
            new File(".test/fileTestsFH/exact.txt"),
            new File(".test/fileTestsFH/file.txt"),
            new File(".test/fileTestsFH/index0.html"),
            new File(".test/fileTestsFH/index1.html"),
            new File(".test/fileTestsFH/negative.html"),
            new File(".test/fileTestsFH/test.cfg"),
            new File(".test/fileTestsFH/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));

        Main.main(null);
        Thread.sleep(1000);

        final String url = "http://localhost:" + Vars.Test.getTestPort() + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTestsFH").getAbsolutePath());

        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "firstFH", TestResponse.getResponseContent(URI.create(url + "/index1.html")));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","firstFH", TestResponse.getResponseContent(URI.create(url + "/index0.html")));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","firstFH", TestResponse.getResponseContent(URI.create(url + "/exact.txt")));
        Assert.assertEquals("Default with *.cfg scope should render file","firstFH", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with file.* scope should render file","firstFH", TestResponse.getResponseContent(URI.create(url + "/test.cfg")));
        Assert.assertEquals("Default with *.log scope should render file","firstFH", TestResponse.getResponseContent(URI.create(url + "/test.log")));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", TestResponse.getResponseContent(URI.create(url + "/negative.html")));
    }

}
