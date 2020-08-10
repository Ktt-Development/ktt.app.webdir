import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.Vars;
import org.junit.*;

import java.io.File;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class IndexTest {

    @Test
    public void test() throws ExecutionException, InterruptedException{
        Vars.Test.safemode = true;
        Vars.Test.server = true;

        final File file = new File(".root/indexTests/index.html");
        final String content = String.valueOf(System.currentTimeMillis());
        TestFile.createTestFile(file,content);

        final int port = Vars.Test.assignPort();

        Vars.Test.disableLogger = true;
        Main.main(null);

        final String url = "http://localhost:" + port;
        // test index and no index
        Assert.assertEquals("Server should resolve index.html to '/'", content, getResponseContent(URI.create(url + "/indexTests")));
        Assert.assertEquals("index.html should still work as expected", content, getResponseContent(URI.create(url + "/indexTests/index")));
    }

    private String getResponseContent(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .build();

        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

}
