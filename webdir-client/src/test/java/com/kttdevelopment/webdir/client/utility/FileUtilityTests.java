package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtilityTests {

      @Test
    public void testFileName(){
        // test with ext
        Assertions.assertEquals("filename", FileUtility.getFileName("filename.ext"));
        // test 'fake' ext
        Assertions.assertEquals("filename.no", FileUtility.getFileName("filename.no.ext"));
        // test no ext
        Assertions.assertEquals("filename", FileUtility.getFileName("filename"));
    }

    @Test
    public void testExtension(){
        // test with ext
        Assertions.assertEquals("ext",FileUtility.getFileExtension("filename.ext"));
        // test 'fake' ext
        Assertions.assertEquals("ext",FileUtility.getFileExtension("filename.no.ext"));
        // test no ext
        Assertions.assertEquals("",FileUtility.getFileExtension("filename"));
    }

    @TempDir
    public final File dir = new File(String.valueOf(UUID.randomUUID()));

    @Test
    public void testUnblockedFile() throws IOException{
        // test unblocked
        final String
            name = String.valueOf(System.currentTimeMillis()),
            ext = "txt";
        File file = new File(dir,name + '.' + ext);
        Assertions.assertEquals(file, FileUtility.getFreeFile(file));
        // test blocked
        Assertions.assertTrue(new File(dir, file.getName()).createNewFile());
        Assertions.assertEquals(new File(dir,name + " (0)." + ext), FileUtility.getFreeFile(file));
        // test double blocked
        Assertions.assertTrue(new File(dir, name + " (0)." + ext).createNewFile());
        Assertions.assertEquals(new File(dir,name + " (1)." + ext), FileUtility.getFreeFile(file));
    }

}
