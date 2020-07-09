import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
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

        final String out = String.format("---\n%s\n---\n%s",frontMatter,content);

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

        final String out = String.format("---\n%s\n---",frontMatter);

        YamlFrontMatter yml = new YamlFrontMatterReader(out).read();
        Assert.assertFalse("Malformed front matter should not be read as front matter",yml.hasFrontMatter());
        Assert.assertEquals("Content without front matter should not remove invalid front matter",out,yml.getContent());

        yml = new YamlFrontMatterReader(frontMatter).read();
        Assert.assertFalse("Content without front matter dashes should not be read as front matter",yml.hasFrontMatter());
        Assert.assertEquals("Content without front matter dashes should not remove anything",frontMatter,yml.getContent());
    }

    @Test
    public void testImports(){
        final String frontMatter =
            "import: /src/test/resources/frontMatter/import.yml";

        final String out = String.format("---\n%s\n---",frontMatter);

        final YamlFrontMatter yml = new YamlFrontMatterReader(out).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter should have 1 value from import and 3 imported values",4, config.toMap().size());
    }

    @Test
    public void testSubImports(){
        final String frontMatter =
            "import: /src/test/resources/frontMatter/import_relative.yml";

        final String out = String.format("---\n%s\n---",frontMatter);

        final YamlFrontMatter yml = new YamlFrontMatterReader(out).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter should have 2 values from import and import_relative and 3 imported values",5,config.toMap().size());
    }

    @Test
    public void testSubImportsNoExt(){
        final String frontMatter =
            "import: /src/test/resources/frontMatter/import_relative_noext.yml";

        final String out = String.format("---\n%s\n---",frontMatter);

        final YamlFrontMatter yml = new YamlFrontMatterReader(out).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter should have 2 values from import and import_relative and 3 imported values",5,config.toMap().size());
    }

}
