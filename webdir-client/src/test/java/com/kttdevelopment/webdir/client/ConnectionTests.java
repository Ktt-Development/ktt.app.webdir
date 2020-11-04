package com.kttdevelopment.webdir.client;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class ConnectionTests {

    private static String head, ignore, path;

    @BeforeAll
    public static void before() throws IOException{
        new File("config.yml").deleteOnExit();
        new File("permissions.yml").deleteOnExit();
        new File("_site").deleteOnExit();

        head = "http://localhost:" + 8080;
        ignore = Files.readString(new File("../.gitignore").toPath());
        path = head + "/files/" + new File("../.gitignore").getCanonicalPath().replace('\\', '/');
    }

    @AfterAll
    public static void cleanup(){
        LoggerServiceTests.clearLogFiles();
    }

    @Test
    public void testBlocked() throws IOException{
        {
            // config dependencies (port must not be 80).
            Files.write(new File("config.yml").toPath(), "port: 8080\nserver: true\nsafe: true".getBytes());

            // permissions dependencies
            Files.write(new File("permissions.yml").toPath(), "users:\n  127.0.0.1:\n    options:\n      connection-limit: 0".getBytes());
        }
        Main.main(null);

        Assertions.assertNull(getResponseContent(path));
    }



    @Test
    public void testUnblocked() throws IOException{
        {
            // config dependencies (port must not be 80).
            Files.write(new File("config.yml").toPath(), "port: 8080\nserver: true\nsafe: true".getBytes());

            // permissions dependencies
            Files.write(new File("permissions.yml").toPath(), "users:\n  127.0.0.1:\n    options:\n      connection-limit: -1".getBytes());
        }
        Main.main(null);

        Assertions.assertEquals(ignore, getResponseContent(path), String.format("Failed to read %s (make sure tests are run with Windows OS)", path));
    }

     private String getResponseContent(final String url){
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .build();

        try{
            return HttpClient
                .newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .get();
        }catch(final InterruptedException | ExecutionException ignored){
            return null;
        }
    }

}
