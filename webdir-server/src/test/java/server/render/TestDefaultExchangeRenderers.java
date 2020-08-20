package server.render;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestDefaultExchangeRenderers {

    @Test
    public void testDefaultExchangeRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index0.html\n" +
            "exchangeRenderers:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index1.html\n" +
            "exchangeRenderers:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /defaultTestsEx/index0.html\n" +
            "     - /defaultTestsEx/index1.html\n" +
            "exchangeRenderers: secondEx",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTestsEx/negative.html\n" +
            "    - \"!/defaultTestsEx/negative.html\"\n" +
            "exchangeRenderers:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTestsEx/exact.txt\n" +
            "    - /defaultTestsEx/*.cfg\n" +
            "    - /defaultTestsEx/file.*\n" +
            "    - \"*.log\"\n" +
            "exchangeRenderers:\n" +
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

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:" + port + "/defaultTestsEx";

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

}
