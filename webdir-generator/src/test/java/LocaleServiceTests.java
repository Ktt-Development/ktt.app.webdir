import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;
import org.junit.*;

import java.util.Locale;

public class LocaleServiceTests {

    @Test
    public void testWatching(){
        Vars.Test.testmode = true;
        Locale.setDefault(Locale.US);
        final LocaleService locale = new LocaleService("locale/bundle");
        locale.setLocale(Locale.JAPAN);

        final LocaleBundleImpl bundle = new LocaleBundleImpl("locale/bundle");

        final String testKey = "noSF";

        Assert.assertNotEquals("Unchanged bundle should not have same locale as changed bundle",locale.getString(testKey),bundle.getString(testKey));
        locale.addWatchedLocale(bundle);
        Assert.assertEquals("After adding to watch both bundles should have same locale",locale.getString(testKey),bundle.getString(testKey));
        locale.setLocale(Locale.US);
        Assert.assertEquals("After changing locale both bundles should have same locale",locale.getString(testKey),bundle.getString(testKey));

    }

    @Test
    public void testGetString(){
        Locale.setDefault(Locale.US);
        final LocaleBundle bundle = new LocaleBundleImpl("locale/bundle");

        final String literalKey = "noSF";
        final String literalValue = "[English] string with no format";
        final String formatKey = "SF";
        final String formatValue = "[English] String with two formats %s %s";
        final String formatArg = "this", formatArg2 = "and";

        Assert.assertEquals("Get string on key should return correct value",literalValue,bundle.getString(literalKey));

        Assert.assertEquals("Formatted string should return literal value given no parameters",formatValue,bundle.getString(formatKey));
        Assert.assertEquals("Formatted string should return value with format applied", String.format(formatValue, formatArg,formatArg2),bundle.getString(formatKey,formatArg,formatArg2));
        Assert.assertEquals("Formatted string with insufficient args should return literal value", formatValue,bundle.getString(formatKey,formatArg));
    }

}
