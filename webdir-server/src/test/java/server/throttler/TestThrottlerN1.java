package server.throttler;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.server.Main;
import org.junit.Assert;
import org.junit.Test;
import utility.TestFile;
import utility.TestResponse;

import java.io.File;
import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestThrottlerN1 {

    @Test
    public void testN1() throws InterruptedException, BrokenBarrierException{
        Vars.Test.safemode = true;
        Vars.Test.server = true;

        final int conn = -1;
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

        final CyclicBarrier pause = new CyclicBarrier(3);

        final AtomicBoolean failed = new AtomicBoolean(false);
        final Thread th1 =
        new Thread(() -> {
            try{ pause.await();
            }catch(final InterruptedException | BrokenBarrierException ignored){ }
            try{
                final int rcode = TestResponse.getResponseCode(URI.create(url + "/test"));
                if(rcode != 200) failed.set(true);
                Assert.assertEquals("Server should have returned response for a connection limit of " + conn, 200, rcode);
            }catch(ExecutionException | InterruptedException e){
                failed.set(true);
                Assert.fail("Server should have returned response for a connection limit of " + conn);
            }
        });
        final Thread th2 =
        new Thread(() -> {
            try{ pause.await();
            }catch(final InterruptedException | BrokenBarrierException ignored){ }
            try{
                final int rcode = TestResponse.getResponseCode(URI.create(url + "/test"));
                if(rcode != 200) failed.set(true);
                Assert.assertEquals("Server should have returned response for a connection limit of " + conn, 200, rcode);
            }catch(ExecutionException | InterruptedException e){
                failed.set(true);
                Assert.fail("Server should have returned response for a connection limit of " + conn);
            }
        });

        th1.start();
        th2.start();
        pause.await();
        th1.join();
        th2.join();
        if(failed.get()) Assert.fail();
    }

}
