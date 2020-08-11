package tests;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import org.junit.*;
import utility.TestFile;

import java.io.File;
import java.io.IOException;

public class ConfigurationFileTests {

    private final String testKey = "test", testValue = String.valueOf(System.currentTimeMillis());

    @Test
    public void testDefault(){
        final ConfigurationSection def = new ConfigurationSectionImpl();
        def.set(testKey,testValue);

        final ConfigurationSection config = new ConfigurationSectionImpl();

        Assert.assertTrue("Blank configuration should have no values but had " + config.toMap().size(),config.toMap().isEmpty());
        config.setDefault(def);
        Assert.assertEquals("Configuration with missing key should use default value",testValue,config.getString(testKey));
    }

    @Test
    public void testFile() throws IOException{
        final File testFile = new File("src/test/resources/config/testFileRead.yml");
        TestFile.createTestFile(testFile, testKey + ": " + testValue);

        final ConfigurationFile config = new ConfigurationFile();
        Assert.assertTrue("Configuration before file load should have no values but had " + config.toMap().size(),config.toMap().isEmpty());
        config.load(testFile);
        Assert.assertEquals("Configuration after load should have test value:" + "\nContent:{\n" + testKey + ": " + testValue + "\n}",testValue,config.getString(testKey));
    }

}
