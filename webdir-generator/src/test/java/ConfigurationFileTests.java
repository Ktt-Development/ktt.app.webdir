import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationFile;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigurationFileTests {

    @Test
    public void testDefault(){
        final String defKey = "random", defValue = "value";
        final ConfigurationSection def = new ConfigurationSectionImpl();
        def.set(defKey,defValue);

        final ConfigurationFile config = new ConfigurationFileImpl();

        Assert.assertTrue("Blank configuration impl should have no values",config.toMap().isEmpty());
        config.setDefault(def);
        Assert.assertEquals("Configuration file with default set should use default fallback",defValue,config.getString(defKey));
    }

    @Test
    public void testFile(){
        final String testKey = "time", testValue = String.valueOf(System.currentTimeMillis());
        final File target = new File("src/test/resources/config/config.yml");
        try{
            Files.write(target.toPath(),(testKey + ": " + testValue).getBytes());
        }catch(final IOException ignored){
            Assert.fail("Failed to write data to config");
        }
        final ConfigurationFile config = new ConfigurationFileImpl(target);
        Assert.assertTrue("Blank configuration impl should have no values",config.toMap().isEmpty());

        // read
        config.reload();

        Assert.assertEquals("After reload written key and value should match",testValue,config.getString(testKey));

        // reload & read

        config.set(testKey,System.currentTimeMillis());
        final String before = config.getString(testKey);
        config.reload();
        Assert.assertNotEquals("After reload any previous set values should revert to file",before,config.getString(testKey));

        // write & read
        config.set(testKey,System.currentTimeMillis());
        final String after = config.getString(testKey);
        config.save();
        config.reload();

        Assert.assertEquals("After save and reload written key and value should match",after,config.getString(testKey));
    }

}
