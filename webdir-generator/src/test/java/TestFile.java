import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

abstract class TestFile {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createTestFile(final File file, final String content){
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        file.deleteOnExit();
        try{
            Files.write(file.toPath(), content.getBytes());
        }catch(final IOException e){
            e.printStackTrace();
            Assert.fail("Failed to write to test import file");
        }
    }

}
