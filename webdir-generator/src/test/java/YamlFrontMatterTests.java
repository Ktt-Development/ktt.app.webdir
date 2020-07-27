import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import org.junit.*;

import java.io.File;
import java.util.Map;

public class YamlFrontMatterTests {

    @Test
    public void testValidRead(){
        final String frontMatter = getFrontMatter(Map.of(
           "some"   ,"values",
           "other"  ,true,
           "and"    ,34
        ));

        final String content = "random content";

        final String test = String.format("---\n%s\n---\n%s",frontMatter,content);

        final YamlFrontMatter yml = new YamlFrontMatterReader(test).read();

        Assert.assertTrue("Valid front matter/content did not return true for #hasFrontMatter:" + '\n' + test,yml.hasFrontMatter());

        Assert.assertEquals("Source front matter should match #getFrontMatterAsString",frontMatter,yml.getFrontMatterAsString());
        Assert.assertEquals("Source content should match #getContent",content,yml.getContent());
    }

    @Test
    public void testInvalidRead(){
        final String malformedFrontMatter = getFrontMatter(Map.of(
            "{some" ,"values",
            "ot}her",true,
            "an&d:" ,34
        ));
        String test = String.format("---\n%s\n---",malformedFrontMatter);

        YamlFrontMatter yml = new YamlFrontMatterReader(test).read();
        Assert.assertFalse("Malformed front matter did not return false for #hasFrontMatter:" + '\n' + test,yml.hasFrontMatter());
        Assert.assertEquals("Content with malformed front matter should not remove it from content",test,yml.getContent());

        yml = new YamlFrontMatterReader(malformedFrontMatter).read();
        Assert.assertFalse("Content without front matter dashes did not return false for #hasFrontMatter:" + '\n' + malformedFrontMatter,yml.hasFrontMatter());
        Assert.assertEquals("Content without front matter dashes should not remove it from content",malformedFrontMatter,yml.getContent());

        test = String.format("%s\n---",malformedFrontMatter);
        yml = new YamlFrontMatterReader(test).read();
        Assert.assertFalse("Content without top front matter dashes did not return false for #hasFrontMatter:" + '\n' + test,yml.hasFrontMatter());
        Assert.assertEquals("Content without top front matter dashes should not remove it from content",test,yml.getContent());

        test = String.format("---\n%s\n",malformedFrontMatter);
        yml = new YamlFrontMatterReader(test).read();
        Assert.assertFalse("Content without bottom front matter dashes did not return false for #hasFrontMatter:" + '\n' + test,yml.hasFrontMatter());
        Assert.assertEquals("Content without bottom front matter dashes should not remove it from content",test,yml.getContent());

        test = String.format("---\n%s\n------",malformedFrontMatter);
        yml = new YamlFrontMatterReader(test).read();
        Assert.assertFalse("Content with extra bottom front matter dashes did not return false for #hasFrontMatter:" + '\n' + test,yml.hasFrontMatter());
        Assert.assertEquals("Content with extra bottom front matter dashes should not remove it from content",test,yml.getContent());
    }

    @Test
    public void testImports(){
        final String testFileContent = getFrontMatter(Map.of(
            "some"  ,"value",
            "with"  ,0,
            "others",true
        ));

        final File testFile = new File("src/test/resources/frontMatterTests/testImports.yml");
        TestFile.createTestFile(testFile,testFileContent);

        // test
        final String frontMatter = getFrontMatter(Map.of(
            "import","/src/test/resources/frontMatterTests/" + testFile.getName()
        ));

        final String test = String.format("---\n%s\n---",frontMatter);

        final YamlFrontMatter yml = new YamlFrontMatterReader(test).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter should have 1 value from import and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4, config.toMap().size());
    }

    @Test
    public void testImportsVariedContext(){
        final String testFileContent = getFrontMatter(Map.of(
            "some"  ,"value",
            "with"  ,0,
            "others",true
        ));
        final File testFile = new File("src/test/resources/frontMatterTests/testImportsVaried.yml");
        TestFile.createTestFile(testFile,testFileContent);
        // test different slash pos
        String frontMatter = getFrontMatter(Map.of(
            "import","src/test/resources/frontMatterTests/" + testFile.getName() + "/"
        ));
        String test = String.format("---\n%s\n---",frontMatter);

        YamlFrontMatter yml = new YamlFrontMatterReader(test).read();
        ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter with trailing slash only did not import:" + '\n' + "front matter should have 1 value from import and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4, config.toMap().size());
        // test back slash
        frontMatter = getFrontMatter(Map.of(
            "import","\\src\\test\\resources\\frontMatterTests\\" + testFile.getName()
        ));
        test = String.format("---\n%s\n---",frontMatter);

        yml = new YamlFrontMatterReader(test).read();
        config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter with different slash (\\ instead of /) did not import:" + '\n' + "front matter should have 1 value from import and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4, config.toMap().size());

        // test no slash
        frontMatter = getFrontMatter(Map.of(
                "import","src/test/resources/frontMatterTests/" + testFile.getName()
        ));
        test = String.format("---\n%s\n---",frontMatter);

        yml = new YamlFrontMatterReader(test).read();
        config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter with no leading/trailing slash did not import:" + '\n' + "front matter should have 1 value from import and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4, config.toMap().size());
    }

    @Test
    public void testRelativeImports(){
        final String testFileContent = getFrontMatter(Map.of(
            "some"  ,"value",
            "with"  ,0,
            "others",true
        ));
        final File testFile = new File("src/test/resources/frontMatterTests/testImportRelative.yml");
        TestFile.createTestFile(testFile,testFileContent);
        //
        final String frontMatter = getFrontMatter(Map.of(
            "import_relative",testFile.getPath()
        ));
        String test = String.format("---\n%s\n---",frontMatter);
        YamlFrontMatter yml = new YamlFrontMatterReader(test).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter should have 1 values from import and import_relative and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4,config.toMap().size());
    }

    @Test
    public void testRelativeImportsNoExtension(){
        final String testFileContent = getFrontMatter(Map.of(
            "some"  ,"value",
            "with"  ,0,
            "others",true
        ));
        final File testFile = new File("src/test/resources/frontMatterTests/testImportRelativeNoExt.yml");
        TestFile.createTestFile(testFile,testFileContent);
        //
        final String frontMatter = getFrontMatter(Map.of(
            "import_relative",testFile.getPath().substring(0,testFile.getPath().lastIndexOf('.'))
        ));
        String test = String.format("---\n%s\n---",frontMatter);
        YamlFrontMatter yml = new YamlFrontMatterReader(test).read();
        final ConfigurationSection config = YamlFrontMatter.loadImports(yml.getFrontMatter());

        Assert.assertEquals("Front matter with no extension for import did not import" + '\n' + "front matter should have 1 values from import and import_relative and 3 imported values:" + "\nTest: {\n" + test + "\n}\nImported: {\n" + testFileContent + "\n}",4,config.toMap().size());
    }

    @Test
    public void testLoopImports(){
        final File testImport1File = new File("src/test/resources/frontMatterTests/testImportLoop1.yml");
        final File testImport2File = new File("src/test/resources/frontMatterTests/testImportLoop2.yml");

        final String testImport1 = getFrontMatter(Map.of(
            "import_relative", testImport2File.getName()
        ));
        TestFile.createTestFile(testImport1File,testImport1);

        final String testImport2 = getFrontMatter(Map.of(
            "import_relative", testImport1File.getName()
        ));
        TestFile.createTestFile(testImport2File,testImport2);
        //
        final String frontMatter = getFrontMatter(Map.of(
            "import","/src/test/resources/frontMatterTests/" + testImport1File.getName()
        ));
        final String test = String.format("---\n%s\n---",frontMatter);

        final YamlFrontMatter yml = new YamlFrontMatterReader(test).read();

        try{
            YamlFrontMatter.loadImports(yml.getFrontMatter());
        }catch(final StackOverflowError ignored){
            Assert.fail("Import loop prevention did not work correctly" + "\nTest: {\n" + test + "\n}\nImport1: {\n" + testImport1 + "\n}\nImport2: {\n" + testImport2 + "\n}");
        }

    }

    private String getFrontMatter(final Map<String,Object> map){
        final StringBuilder OUT = new StringBuilder();
        map.forEach((k, v) -> OUT.append(k).append(':').append(' ').append(v).append('\n'));
        OUT.deleteCharAt(OUT.length()-1);
        return OUT.toString();
    }

}
