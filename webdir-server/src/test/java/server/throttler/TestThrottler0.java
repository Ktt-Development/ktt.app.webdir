package server.throttler;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class TestThrottler0 {

    @Test
    public void test0(){
        Vars.Test.safemode = true;
        Vars.Test.server = true;

        final int conn = 0;
        final String perm =
            "groups:\n" +
            "  default:\n" +
            "    options:\n" +
            "      default: true\n" +
            "      connection-limit: " + conn;

        TestFile.createTestFile(new File("permissions.yml").getAbsoluteFile(), perm);
        TestFile.createTestFile(new File(".root/test.html"),"");

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:" + port;

        try{
            TestResponse.getResponseContent(URI.create(url + "/test"));
            Assert.fail("Server should not have returned response for a connection limit of " + conn);
        }catch(final ExecutionException | InterruptedException ignored){ }
    }

}
