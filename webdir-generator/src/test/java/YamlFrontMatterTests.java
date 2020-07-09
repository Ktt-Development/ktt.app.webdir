import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import org.junit.*;

public class YamlFrontMatterTests {

    @Test
    public void testValidRead(){
        final String frontMatter =
            "some: values" + '\n' +
            "other: true"  + '\n' +
            "and: 34";

        final String content = "random content";

        final String out =
            "---" + '\n' +
            frontMatter + '\n' +
            "---" + '\n' +
            content;

        final YamlFrontMatter yml = new YamlFrontMatterReader(out).read();

        Assert.assertTrue("Valid front matter should be valid",yml.hasFrontMatter());

        Assert.assertEquals("Front matter source should match front matter",frontMatter,yml.getFrontMatterAsString());
        Assert.assertEquals("Content source should match content",content,yml.getContent());
    }

    @Test
    public void testInvalidRead(){
        final String frontMatter =
            "{some: values" + '\n' +
            "ot}her: true"  + '\n' +
            "an&d: 34";

        final String out =
            "---" + '\n' +
            frontMatter + '\n' +
            "---";

        YamlFrontMatter yml = new YamlFrontMatterReader(out).read();
        Assert.assertFalse("Malformed front matter should not be read as front matter",yml.hasFrontMatter());
        Assert.assertEquals("Content without front matter should not remove invalid front matter",out,yml.getContent());

        yml = new YamlFrontMatterReader(frontMatter).read();
        Assert.assertFalse("Content without front matter dashes should not be read as front matter",yml.hasFrontMatter());
        Assert.assertEquals("Content without front matter dashes should not remove anything",frontMatter,yml.getContent());
    }

    @Test @Ignore
    public void testImports(){
        // todo
    }

}
