import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.generator.*;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigServiceTests {

    @Test
    public void testValid() throws IOException{
        final String key = "key", value = "value";
        final String defaultKey = "default", defaultValue = "value";
        final File configFile = new File("src/test/resources/configTests/testConfig.yml");
        TestFile.createTestFile(configFile,key + ": " + value);

        final String defaultResource = "/configTests/defaultConfig.yml";

        final ConfigService config = new ConfigService(configFile,defaultResource);

        Assert.assertEquals("Valid config file did not return the correct value",value,config.getConfig().getString(key));
        Assert.assertEquals("Config service did not return default value when using a config with missing key",defaultValue,config.getConfig().getString(defaultKey));
    }

    @Test
    public void testMissingDef() throws IOException{
        final String defaultResource = "/configTests/missingDefault.yml";

        try{
            new ConfigService(new File("null"), defaultResource);
            Assert.fail("Config Service failed to throw null exception for missing default config");
        }catch(final NullPointerException ignored){ }
    }

    @Test
    public void testMalformedDef() throws IOException{
        final String defaultResource = "/configTests/malformedDefault.yml";

        try{
            new ConfigService(new File("null"),defaultResource);
            Assert.fail("Config Service failed to throw yaml exception for malformed default config");
        }catch(final ClassCastException | YamlException ignored){ }
    }

    @Test
    public void testMissingConfig() throws IOException{
        final String defaultKey = "default", defaultValue = "value";
        final String defaultResource = "/configTests/defaultConfig.yml";
        final File missingConfig = new File("src/test/resources/configTests/missing.yml");
        if(missingConfig.exists() && !missingConfig.delete())
            Assert.fail("Failed to delete config file for testing");
        missingConfig.deleteOnExit();

        final ConfigService config = new ConfigService(missingConfig,defaultResource);

        Assert.assertEquals("Config service did not return default value when using a missing config",defaultValue,config.getConfig().getString(defaultKey));
        Assert.assertTrue("Config service did not create a new config file when using a missing config",missingConfig.exists());
        Assert.assertEquals("New file created by config service did not match default config", defaultKey + ": " + defaultValue, Files.readString(missingConfig.toPath()));
    }

    @Test
    public void testMalformedConfig() throws IOException{
        final String defaultKey = "default", defaultValue = "value";
        final String defaultResource = "/configTests/defaultConfig.yml";
        final File malformedConfig = new File("src/test/resources/configTests/malformed.yml");
        TestFile.createTestFile(malformedConfig, String.valueOf(System.currentTimeMillis()));

        final ConfigService config = new ConfigService(malformedConfig,defaultResource);
        Assert.assertEquals("Malformed config did not return default value",defaultValue,config.getConfig().getString(defaultKey));
    }

}
