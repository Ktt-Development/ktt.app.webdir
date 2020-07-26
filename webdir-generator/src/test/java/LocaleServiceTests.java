import com.kttdevelopment.webdir.api.serviceprovider.LocaleBundle;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;
import org.junit.*;

import java.util.Locale;

public class LocaleServiceTests {

    private static final Locale def = Locale.getDefault();

    private static final String noSFKey = "noSF", SFKey = "SF";
    private static final String noSFContentEN = "[English] string with no format", SFContentEN = "[English] String with two formats %s %s";

    private static final String bundle = "localeTests/bundle";

    @BeforeClass // resources folder can not be added to for #getResource
    public static void before(){
        Vars.Test.testmode = true;
        Locale.setDefault(Locale.US);
    }

    @AfterClass
    public static void after(){
        Vars.Test.testmode = false;
        Locale.setDefault(def);
    }

    @Test
    public void testWatching(){
        final LocaleService locale = new LocaleService(bundle);
        locale.setLocale(Locale.JAPAN);

        final LocaleBundleImpl testBundle = new LocaleBundleImpl(bundle);

        Assert.assertNotEquals("Bundle that has not been added to locale service should not switch to locale used by that service",locale.getString(noSFKey),testBundle.getString(noSFKey));
        locale.addWatchedLocale(testBundle);
        Assert.assertEquals("Bundle that was added to locale service should switch to locale used by that service",locale.getString(noSFKey),testBundle.getString(noSFKey));
        locale.setLocale(Locale.US);
        Assert.assertEquals("Bundle that was added to locale service should switch to locale used by the service when the service changes locale",locale.getString(noSFKey),testBundle.getString(noSFKey));

    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testGetString(){
        final LocaleBundle testBundle = new LocaleBundleImpl(bundle);

        final String formatArg = "this", formatArg2 = "and";

        Assert.assertEquals("Using #getString for " + noSFKey + " did not return correct value for EN",noSFContentEN,testBundle.getString(noSFKey));

        Assert.assertEquals("Using #getString for " + SFKey + " should return unformatted string",SFContentEN,testBundle.getString(SFKey));
        Assert.assertEquals("Using #getString for " + SFKey + " with args [" + formatArg + ", " + formatArg2 + "] should return formatted string", String.format(SFContentEN, formatArg,formatArg2),testBundle.getString(SFKey,formatArg,formatArg2));
        Assert.assertEquals("Using #getString with one format arg for " + SFKey + " (requires two) should return unformatted string", SFContentEN,testBundle.getString(SFKey,formatArg));
    }

}
