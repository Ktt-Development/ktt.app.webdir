package server.render;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestExchangeRenderers {

    @Test
    public void testExchangeRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.server = true;

        Map.of(
            new File(".root/renderTestsEx/renderOrder.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n" +
            "  - secondEx\n" +
            "---",
            new File(".root/renderTestsEx/renderReverseOrder.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - secondEx\n" +
            "  - plugin: ExchangeRenderTests\n" +
            "    renderer: firstEx\n" +
            "  - exceptionEx\n" +
            "---",
            new File(".root/renderTestsEx/renderExactDuplicate.html"),
            "---\n" +
            "exchangeRenderer:\n" +
            "  - plugin: ExchangeDuplicateRenderTests\n" +
            "    renderer: firstEx\n" +
            "---"
        ).forEach(TestFile::createTestFile);

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:" + port + "/renderTestsEx";

        Assert.assertEquals("Renderers lower on the list are expected to render last", "secondEx", TestResponse.getResponseContent(URI.create(url + "/renderOrder")));
        Assert.assertEquals("Renderers lower on the list are expected to render last","firstEx",TestResponse.getResponseContent(URI.create(url + "/renderReverseOrder")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATEEX", TestResponse.getResponseContent(URI.create(url + "/renderExactDuplicate")));
    }

}
