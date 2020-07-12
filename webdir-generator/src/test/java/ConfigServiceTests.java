import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.generator.ConfigService;
import com.kttdevelopment.webdir.generator.Main;
import org.junit.*;

import java.io.File;
import java.io.IOException;

public class ConfigServiceTests {

    @Before
    public void before(){
        Main.testMode = true;
    }

    @Test
    public void testValid() throws IOException{
        final String key = "key", value = "value";
        final String defaultKey = "default", defaultValue = "value";
        final File configFile = new File("src/test/resources/config/valid/config.yml");
        final String defaultResource = "/config/valid/default.yml";

        final ConfigService config = new ConfigService(configFile,defaultResource);

        Assert.assertEquals("Valid config file did not return the correct value",value,config.getConfig().getString(key));
        Assert.assertEquals("Config did not return default value for missing key",defaultValue,config.getConfig().getString(defaultKey));
    }

    @Test
    public void testMissingDef() throws IOException{
        final String defaultResource = "/config/null.yml";

        try{
            new ConfigService(null, defaultResource);
            Assert.fail("Config Service failed to throw null for missing default config");
        }catch(final NullPointerException ignored){ }
    }

    @Test
    public void testMalformedDef() throws IOException{
        final String defaultResource = "/config/malformed.yml";

        try{
            new ConfigService(null,defaultResource);
            Assert.fail("Config Service failed to throw yaml exception for malformed default config");
        }catch(final ClassCastException | YamlException ignored){ }
    }

    @Test
    public void testMissingConfig() throws IOException{
        final String key = "default", value = "value";
        final File configFile = new File("src/test/resources/config/missing/config.yml");
        final String defaultResource = "/config/defaultConfig.yml";

        if(configFile.exists() && !configFile.delete())
            Assert.fail("Failed to delete config file for testing");

        final ConfigService config = new ConfigService(configFile,defaultResource);

        Assert.assertEquals("New config did not return correct value for key",value,config.getConfig().getString(key));
        Assert.assertTrue("Config service did not create a new file",configFile.exists());
        if(configFile.exists())
            //noinspection ResultOfMethodCallIgnored
            configFile.delete();
    }

    @Test
    public void testMalformedConfig() throws IOException{
        final String key = "default", value = "value";
        final File configFile = new File("src/test/resources/config/malformed.yml");
        final String defaultResource = "/config/defaultConfig.yml";

        final ConfigService config = new ConfigService(configFile,defaultResource);

        Assert.assertEquals("Malformed config did not return default value",value,config.getConfig().getString(key));
    }

}
