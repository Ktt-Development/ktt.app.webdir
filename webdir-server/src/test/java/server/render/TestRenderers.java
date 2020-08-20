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

public class TestRenderers {

    @Test
    public void testRenderers() throws ExecutionException, InterruptedException{
        Vars.Test.server = true;

        Map.of(
            new File(".root/renderTests/renderOrder.html"),
            "---\n" +
            "renderers:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "  - second\n" +
            "  - exception\n" +
            "  - firstEx\n" +
            "  - secondEx\n" +
            "---",
            new File(".root/renderTests/renderReverseOrder.html"),
            "---\n" +
            "renderers:\n" +
            "  - second\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "  - firstEx\n" +
            "  - secondEx\n" +
            "---",
            new File(".root/renderTests/renderExactDuplicate.html"),
            "---\n" +
            "renderers:\n" +
            "  - plugin: DuplicateRenderTests\n" +
            "    renderer: first\n" +
            "---"
        ).forEach(TestFile::createTestFile);

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:" + port + "/renderTests";

        Assert.assertEquals("Renderers lower on the list are expected to render last", "second", TestResponse.getResponseContent(URI.create(url + "/renderOrder")));
        Assert.assertEquals("Renderers lower on the list are expected to render last","first",TestResponse.getResponseContent(URI.create(url + "/renderReverseOrder")));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATE", TestResponse.getResponseContent(URI.create(url + "/renderExactDuplicate")));
    }

}
