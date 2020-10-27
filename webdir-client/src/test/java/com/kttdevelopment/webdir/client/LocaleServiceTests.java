package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.locale.LocaleBundleImpl;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;

public class LocaleServiceTests {

    @BeforeAll
    public static void before() throws IOException{
        Assertions.assertTrue(new File("src/test/resources/lang/locale_en_US.yml").exists(), "Locale tests files must be added beforehand to work with classloader (must be compiled)");
        Assertions.assertEquals(Files.readString(new File("src/main/resources/lang/locale_en_US.yml").toPath()), Files.readString(new File("src/test/resources/lang/locale_en_US.yml").toPath()), "Locale from main did not match test locale");
        Assertions.assertNotNull(LocaleServiceTests.class.getClassLoader().getResourceAsStream("lang/locale_en_US.yml"), "No locale resource was found (please recompile the module)");
    }

    @AfterAll
    public static void cleanup(){
        LoggerServiceTests.clearLogFiles();
    }

    @Test
    public void testLocale() throws IOException{
        new File("config.yml").deleteOnExit();
        Main.logger = new LoggerService();
        Main.config = new ConfigService(new File("config.yml"));

        // test queue
        Assertions.assertNotEquals(0, Main.logger.getQueuedLoggerMessages().size());
        Main.locale = new LocaleService("lang/locale");

        // test queue clear
        Assertions.assertEquals(0, Main.logger.getQueuedLoggerMessages().size());

        {
            final LocaleBundleImpl locale = new LocaleBundleImpl("locale_tests/locale");

            // test watch
            Main.locale.setLocale(Locale.JAPAN);
            Assertions.assertEquals(Locale.getDefault(), locale.getLocale());
            Main.locale.addWatchedLocale(locale);
            Assertions.assertEquals(Main.locale.getLocale(), locale.getLocale());

            // test change locale
            Main.locale.setLocale(Locale.US);
            Assertions.assertEquals(Main.locale.getLocale(), locale.getLocale());

            // test get + format + missing
            Assertions.assertEquals("English (US)", Main.locale.getString("name"));
            Assertions.assertEquals("Loading configuration from file [x].", Main.locale.getString("config.constructor.config.start", "[x]"));
            Assertions.assertEquals("Loading configuration from file %s.", Main.locale.getString("config.constructor.config.start"));
        }

        // test inheritance
        {
            final LocaleBundleImpl locale = new LocaleBundleImpl("locale_tests/inherit");
            locale.setLocale(Locale.JAPAN);

            Assertions.assertEquals("true", locale.getString("fallback-5"));
            Assertions.assertEquals("true", locale.getString("fallback-4"));
            Assertions.assertEquals("true", locale.getString("fallback-3"));
            Assertions.assertEquals("true", locale.getString("fallback-2"));
            Assertions.assertEquals("true", locale.getString("fallback-1"));
        }

        // test get null
        Assertions.assertNull(Main.getLocale().getString(UUID.randomUUID().toString()));
    }

}
