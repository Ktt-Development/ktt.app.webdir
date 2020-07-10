import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.locale.LocaleBundleImpl;
import org.junit.*;

import java.util.Locale;

public class LocaleServiceTests {

    @Test
    public void testWatching(){
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

    @Test @Ignore
    public void testGetString(){ // test format args

    }

}
