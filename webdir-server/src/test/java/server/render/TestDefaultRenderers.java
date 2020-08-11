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

public class TestDefaultRenderers {

    @Test
    public void testDefaultRenderers() throws ExecutionException, InterruptedException{
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

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:" + port + "/defaultTests";

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

}
