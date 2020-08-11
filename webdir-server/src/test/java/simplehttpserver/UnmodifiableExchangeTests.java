package simplehttpserver;

import com.kttdevelopment.simplehttpserver.*;
import com.kttdevelopment.webdir.server.httpserver.SimpleHttpExchangeUnmodifiable;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;

public class UnmodifiableExchangeTests {

    @Test
    public void testUnsupported() throws IOException, ExecutionException, InterruptedException, TimeoutException{
        final int port = 20000;
        final SimpleHttpServer server = SimpleHttpServer.create(port);

        final SimpleHttpHandler handler = exchange -> {
            final SimpleHttpExchange exchangeUnmodifiable = new SimpleHttpExchangeUnmodifiable(exchange);
            final Runnable[] testMethods = new Runnable[]{
                exchangeUnmodifiable::getHttpServer,
                exchangeUnmodifiable::getHttpExchange,
                exchangeUnmodifiable::getResponseHeaders,
                exchangeUnmodifiable::getResponseCode,
                () -> exchangeUnmodifiable.setCookie(null),
                () -> exchangeUnmodifiable.setCookie(new SimpleHttpCookie.Builder("k","v").build()),
                exchangeUnmodifiable::getOutputStream,
                () -> {
                    try{ exchangeUnmodifiable.sendResponseHeaders(-1,-1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send(-1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send(new byte[0]);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send(new byte[0],false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send(new byte[0],-1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send(new byte[0],-1,false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send("null");
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send("null",false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send("null",-1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{ exchangeUnmodifiable.send("null",-1,false);
                    }catch(final IOException ignored){ }
                },
                exchangeUnmodifiable::close,
                () -> exchangeUnmodifiable.setAttribute("k", "v"),
                () -> exchangeUnmodifiable.getHttpContext().getServer()
            };

            for(final Runnable method : testMethods){
                try{
                    method.run();
                    Assert.fail("An unsupported operation ran without permission");
                }catch(final UnsupportedOperationException ignored){ }
            }

            try{
                exchange.send("CONTENT", 200);
            }catch(final Exception e){
                e.printStackTrace();
            }
        };

        server.createContext("",handler);
        server.start();

        final HttpRequest request = HttpRequest.newBuilder()
           .uri(URI.create("http://localhost:" + port))
           .build();

        final int code = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::statusCode).get(10, TimeUnit.SECONDS);
        Assert.assertEquals("Test did not receive response from server",code,200);
    }

}
