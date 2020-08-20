package application;

import com.kttdevelopment.webdir.generator.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TestRenderer {

    /*
        plugin-render-tests.jar {
            first, second, exception
        }

        plugin-render-duplicate.jar {
            first -> DUPLICATE
        }
     */

    @Test
    public void testRenderer() throws IOException{
        Map.of(
            new File(".root/renderTests/renderOrder.html"),
            "---\n" +
            "renderers:\n" +
            "  - first\n" +
            "  - second\n" +
            "  - exception\n" +
            "---",
            new File(".root/renderTests/renderReverseOrder.html"),
            "---\n" +
            "renderers:\n" +
            "  - second\n" +
            "  - first\n" +
            "---",
            new File(".root/renderTests/renderExactFirst.html"),
            "---\n" +
            "renderers:\n" +
            "  - plugin: RenderTests\n" +
            "    renderer: first\n" +
            "---",
            new File(".root/renderTests/renderExactDuplicate.html"),
            "---\n" +
            "renderers:\n" +
            "  - plugin: DuplicateRenderTests\n" +
            "    renderer: first\n" +
            "---"
        ).forEach(TestFile::createTestFile);
        Main.main(null);

        Assert.assertEquals("Renders lower on the list are expected to run last (thus overriding any content)", "second", Files.readString(new File("_site/renderTests/renderOrder.html").toPath()));
        Assert.assertEquals("Renders lower on the list are expected to run last (thus overriding any content)","first", Files.readString(new File("_site/renderTests/renderReverseOrder.html").toPath()));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","first", Files.readString(new File("_site/renderTests/renderExactFirst.html").toPath()));
        Assert.assertEquals("Render specifying plugin and renderer are expected to use that renderer","DUPLICATE", Files.readString(new File("_site/renderTests/renderExactDuplicate.html").toPath()));
    }

}
