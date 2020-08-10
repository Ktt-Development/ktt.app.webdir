package application;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;

public class TestSafeMode {

    @Test
    public void testSafeMode(){
        Vars.Test.safemode = true;
        Main.main(null);
        Assert.assertTrue("Safe-mode should not load any plugins", Vars.Main.getPluginLoader().getPlugins().isEmpty());
    }

}
