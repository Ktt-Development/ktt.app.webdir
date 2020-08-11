package application;

import com.kttdevelopment.webdir.generator.*;
import org.junit.Assert;
import org.junit.Test;

public class TestPluginLoading {

    @Test
    public void testPluginLoading(){
        Main.main(null);

        final String[] badPlugins = {
            /*
                - plugin missing yml
                - plugin missing name
                - plugin malformed yml
             */
            "CircularDependency1",
            "CircularDependency2",
            "ConstructorException",
            "MissingDependency",
            "MissingMain",
            "NoExtends",
            "NoMain",
            "ThrowsException",
            "TimedOut"
        };

        final String[] goodPlugins = {
            "Dependency",
            "Dependent",
            "DuplicateRenderTests",
            "RenderTests",
            "Valid"
        };

        final PluginLoader pluginLoader = Vars.Main.getPluginLoader();

        for(final String badPlugin : badPlugins)
            Assert.assertNull("Server should not have loaded plugin: " + badPlugin, pluginLoader.getPlugin(badPlugin));

        for(final String goodPlugin : goodPlugins)
            Assert.assertNotNull("Server should have loaded plugin: " + goodPlugin,pluginLoader.getPlugin(goodPlugin));

        Assert.assertEquals("Server should have only loaded: " + goodPlugins.length + " plugins",goodPlugins.length,pluginLoader.getPlugins().size());
    }

}
