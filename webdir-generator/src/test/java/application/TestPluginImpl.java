package application;

import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.Vars;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestPluginImpl {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testImpl(){
        Main.main(null);

        final WebDirPlugin plugin = Vars.Main.getPluginLoader().getPlugin("Valid");

        Assert.assertNotNull("Valid plugin should be loaded by the server", plugin);

        final PluginYml pluginYml = plugin.getPluginYml();
        Assert.assertEquals("Plugin yml should return correct plugin name","Valid",pluginYml.getPluginName());
        Assert.assertEquals("Plugin dependencies in yml should return no dependencies",0,pluginYml.getDependencies().length);
        Assert.assertEquals("Plugin yml should return correct plugin version","v1",pluginYml.getPluginVersion());
        Assert.assertEquals("First plugin author should be first on list","first",pluginYml.getAuthor());
        Assert.assertEquals("Second plugin author should be last on list","second",pluginYml.getAuthors()[1]);

        Assert.assertEquals("Plugin should be able to get other loaded plugins on the server",plugin,plugin.getPlugin("Valid"));
        Assert.assertEquals("Plugin folder should be plugins folder + plugin name", new File(".plugins/Valid"), plugin.getPluginFolder());
        Assert.assertTrue("Using getPluginFolder should create a folder if it does not exist",new File(".plugins/Valid").exists());
        Assert.assertEquals("Plugin logger name should be plugin name",pluginYml.getPluginName(),plugin.getLogger().getName());

        new File(".plugins/Valid").delete();
    }

}
