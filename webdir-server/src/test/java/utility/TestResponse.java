package utility;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public abstract class TestResponse {

    public static String getResponseContent(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .build();

        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body).get();
    }

    public static int getResponseCode(final URI uri) throws ExecutionException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .build();

        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::statusCode).get();
    }

}
