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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestFileRenderers {

    @Test
    public void testFileRenderers() throws ExecutionException, InterruptedException{
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

        final int port =  Vars.Test.assignPort();
        Main.main(null);
        Thread.sleep(1000); // make sure C:// is loaded

        final String url = "http://localhost:" + port + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTests").getAbsolutePath());

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

}
