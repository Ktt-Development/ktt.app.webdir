package application;

import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.Vars;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestClear {

    @Test
    public void testClear() throws IOException{
        Vars.Test.safemode = true;

        final File testRoot = new File(".root/testClear.html");
        final File testOutput = new File("_site/" + testRoot.getName());

        if(!testRoot.getParentFile().exists() && !testRoot.getParentFile().mkdirs())
            Assert.fail("Failed to create test root directory");
        if(!testRoot.exists() && !testRoot.createNewFile())
            Assert.fail("Failed to create test file");
        Main.main(null);
        Assert.assertTrue("Generator did not copy file from root folder",testOutput.exists());

        if(!testRoot.delete())
            Assert.fail("Failed to delete test file from root");

        Vars.Test.clear = true;
        Main.main(null);
        Assert.assertFalse("Generator did not remove file that was no longer present in root folder",testOutput.exists());
    }

}
