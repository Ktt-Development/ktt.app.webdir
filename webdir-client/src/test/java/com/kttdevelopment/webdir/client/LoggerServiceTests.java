package com.kttdevelopment.webdir.client;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Objects;

public class LoggerServiceTests {

    @Test
    public void testLogger(){
        clearLogFiles();
        Assertions.assertEquals(0, Objects.requireNonNullElse(new File(".").listFiles((dir, name) -> name.endsWith(".log")), new File[0]).length, "The test directory has undeleted log files. Please remove them.");

        // logging files
        final LoggerService service = new LoggerService();

        final File latest = new File("latest.log");
        final File debug = new File("debug.log");
        Assertions.assertTrue(latest.exists());
        Assertions.assertTrue(debug.exists());

        // queued messages
        Assertions.assertFalse(service.getQueuedLoggerMessages().isEmpty());
        
        // duplicate handlers
        service.getLogger("logger");
        Assertions.assertEquals(3, service.getLogger("logger").getHandlers().length);
    }

    @AfterAll
    public static void clearLogFiles(){
        for(final File file : Objects.requireNonNullElse(new File(".").listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".lck")), new File[0]))
            file.deleteOnExit();
    }

}
