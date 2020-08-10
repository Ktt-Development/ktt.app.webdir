package server;

import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.Vars;
import org.junit.*;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class IndexTest {

    @Test
    public void test() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = true;
        Vars.Test.server = true;

        final File file = new File(".root/indexTests/index.html");
        final String content = String.valueOf(System.currentTimeMillis());
        TestFile.createTestFile(file, content);

        int port = (Vars.Test.port = 20003);

        Main.main(null);

        final String url = "http://localhost:" + port;
        // test index and no index
        Assert.assertEquals("Server should resolve index.html to '/'", content, TestResponse.getResponseContent(URI.create(url + "/indexTests")));
        Assert.assertEquals("index.html should still work as expected", content, TestResponse.getResponseContent(URI.create(url + "/indexTests/index")));
    }

}
