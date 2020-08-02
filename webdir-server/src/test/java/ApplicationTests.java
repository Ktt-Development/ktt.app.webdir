import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ApplicationTests {

    @Test
    public void testSafeMode(){
        Vars.Test.safemode = true;
        Main.main(null);
        Assert.assertTrue("Safe-mode should not load any plugins", Vars.Main.getPluginLoader().getPlugins().isEmpty());
    }

     @Test
    public void testClear() throws IOException{
        Vars.Test.safemode = true;
        Vars.Test.clear = false;

        final File testRoot = new File(".root/testClear.html");
        final File testOutput = new File("_site/" + testRoot.getName());

        if(!testRoot.getParentFile().exists() && !testRoot.getParentFile().mkdirs())
            Assert.fail("Failed to create test root directory");
        if(!testRoot.exists() && !testRoot.createNewFile())
            Assert.fail("Failed to create test file");
        com.kttdevelopment.webdir.generator.Main.main(null);
        Assert.assertTrue("Generator did not copy file from root folder",testOutput.exists());

        if(!testRoot.delete())
            Assert.fail("Failed to delete test file from root");
        System.out.println("src[1]: " + new File(".root/testClear.html").exists());
        Vars.Test.clear = true;
        com.kttdevelopment.webdir.generator.Main.main(null);
        Assert.assertFalse("Generator did not remove file that was no longer present in root folder",testOutput.exists());
        Vars.Test.clear = false;
    }

}
