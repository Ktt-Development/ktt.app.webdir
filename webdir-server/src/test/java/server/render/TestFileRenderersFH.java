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
import java.util.logging.Logger;

public class TestFileRenderersFH {

    @Test
    public void testFileRenderersFH() throws ExecutionException, InterruptedException{
        if(!System.getProperty("os.name").toLowerCase().contains("win")){
            Logger.getGlobal().severe("Tests were not designed to run on non-windows systems");
            return;
        }
        
        Vars.Test.server = true;

        Map.of(
            new File(".default/index0.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index0.html\n" +
            "exchangeRenderers:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index1.html\n" +
            "exchangeRenderers:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /C:/*/fileTestsFH/index0.html\n" +
            "     - /C:/*/fileTestsFH/index1.html\n" +
            "exchangeRenderers: secondFH",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsFH/negative.html\n" +
            "    - \"!/C:/*/fileTestsFH/negative.html\"\n" +
            "exchangeRenderers:\n" +
            "  - plugin: FileHandlerRenderTests\n" +
            "    renderer: firstFH\n",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /C:/*/fileTestsFH/exact.txt\n" +
            "    - /C:/*/fileTestsFH/*.cfg\n" +
            "    - /C:/*/fileTestsFH/file.*\n" +
            "    - \"*.log\"\n" +
            "exchangeRenderers:\n" +
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

        final int port = Vars.Test.assignPort();
        Main.main(null);
        Thread.sleep(1000); // make sure C:// is loaded

        final String url = "http://localhost:" + port + ContextUtil.joinContexts(true, false, ServerVars.Config.defaultFilesContext, new File(".test/fileTestsFH").getAbsolutePath());

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
