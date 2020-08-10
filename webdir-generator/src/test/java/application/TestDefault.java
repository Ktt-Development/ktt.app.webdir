package application;

import com.kttdevelopment.webdir.generator.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class TestDefault {

    /*
        default {
            index int
            scope []
        }
     */
    @Test
    public void testDefaultRenderer() throws IOException{
        Map.of(
            new File(".default/index.yml"),
            "default:\n" +
            "  scope:\n" +
            "     - /defaultTests/index.html\n" +
            "renderer: first",
            new File(".default/index1.yml"),
            "default:\n" +
            "  index: 1\n" +
            "  scope:\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer: first",
            new File(".default/index-1.yml"),
            "default:\n" +
            "  index: -1\n" +
            "  scope:\n" +
            "     - /defaultTests/index.html\n" +
            "     - /defaultTests/index1.html\n" +
            "renderer: second",
            new File(".default/negative.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/negative.html\n" +
            "    - \"!/defaultTests/negative.html\"\n" +
            "renderer: first",
            new File(".default/scope.yml"),
            "default:\n" +
            "  scope:\n" +
            "    - /defaultTests/exact.txt\n" +
            "    - /defaultTests/*.cfg\n" +
            "    - /defaultTests/file.*\n" +
            "    - \"*.log\"\n" +
            "renderer: first"
        ).forEach(TestFile::createTestFile);

        // test files
        List.of(
            new File(".root/defaultTests/exact.txt"),
            new File(".root/defaultTests/file.txt"),
            new File(".root/defaultTests/index.html"),
            new File(".root/defaultTests/index1.html"),
            new File(".root/defaultTests/negative.html"),
            new File(".root/defaultTests/test.cfg"),
            new File(".root/defaultTests/test.log")
        ).forEach(file -> TestFile.createTestFile(file, ""));
        Main.main(null);

        // test index and no index
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with index 1 to be used but default with index -1 was used)", "first", Files.readString(new File("_site/defaultTests/index1.html").toPath()));
        Assert.assertEquals("Using default files with same scope should go by priority (expected default with no index (0) to be used but default with index -1 was used)","first", Files.readString(new File("_site/defaultTests/index.html").toPath()));

        // test scope
        Assert.assertEquals("Default with exact scope should render file","first", Files.readString(new File("_site/defaultTests/exact.txt").toPath()));
        Assert.assertEquals("Default with *.cfg scope should render file","first", Files.readString(new File("_site/defaultTests/test.cfg").toPath()));
        Assert.assertEquals("Default with file.* scope should render file","first", Files.readString(new File("_site/defaultTests/test.cfg").toPath()));
        Assert.assertEquals("Default with *.log scope should render file","first", Files.readString(new File("_site/defaultTests/test.log").toPath()));

        // test negative scope
        Assert.assertEquals("Default with negation ! scope should not render file","", Files.readString(new File("_site/defaultTests/negative.html").toPath()));
    }

}
