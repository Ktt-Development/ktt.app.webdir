package server.render;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestFileRenderersEx {

    @Test
    public void testFileRenderersEx() throws ExecutionException, InterruptedException{
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

        final int port = Vars.Test.assignPort();
        Main.main(null);
        Thread.sleep(Duration.ofSeconds(6).toMillis()); // make sure C:// is loaded

        final String url = "http://localhost:" + port + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTestsEx").getAbsolutePath());

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

}
