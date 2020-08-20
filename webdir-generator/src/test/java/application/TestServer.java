package application;

import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.Vars;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class TestServer {
    
    @Test
    public void testServer() throws ExecutionException, InterruptedException, IOException{
        Vars.Test.safemode = true;
        Vars.Test.server = true;

        final int port = Vars.Test.assignPort();
        Main.main(null);

        final String url = "http://localhost:%s/%s";

        final String target = String.valueOf(System.currentTimeMillis());
        final Path targetFile = new File(".root/" + target + ".html").toPath();

        // test none
        try{
            Assert.assertNull(
                "Referencing a non-existent page should return null content",
                getResponseContent(URI.create(String.format(url, port, target)))
            );
        }catch(final Exception e){
            if(!e.getMessage().contains("header parser received no bytes"))
                Assert.fail("Referencing a non-existent page should return no content");
        }

        // test add
        final String value = String.valueOf(System.currentTimeMillis());
        Files.write(targetFile,value.getBytes());

        Thread.sleep(1000 * 2); // give time for server to process

        Assert.assertEquals(
            "Server should be able to retrieve newly added file",
            value,
            getResponseContent(URI.create(String.format(url,port,target)))
        );

        // test mod
        final String newValue = String.valueOf(System.currentTimeMillis());
        Files.write(targetFile,newValue.getBytes());

        Thread.sleep(1000 * 2); // give time for server to process

        Assert.assertEquals(
            "Server should be able to retrieve modified file",
            newValue,
            getResponseContent(URI.create(String.format(url,port,target)))
        );

        // test del
        Files.delete(targetFile);
        try{
            Assert.assertNull(
                "Referencing a deleted page should return null content",
                getResponseContent(URI.create(String.format(url, port, System.currentTimeMillis())))
            );
        }catch(final Exception e){
            if(!e.getMessage().contains("header parser received no bytes"))
                Assert.fail("Referencing a deleted page should return null content");
        }
    }

    //

    private String getResponseContent(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .build();

        return HttpClient.newHttpClient().sendAsync(request,HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

}
