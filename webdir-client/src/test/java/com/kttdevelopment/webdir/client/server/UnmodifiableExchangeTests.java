package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.*;
import com.kttdevelopment.webdir.client.server.unmodifiable.SimpleHttpExchangeUnmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;

public class UnmodifiableExchangeTests {

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException, TimeoutException{
        final int port = 8080;
        final SimpleHttpServer server = SimpleHttpServer.create(port);

        final SimpleHttpHandler handler = exchange -> {
            final SimpleHttpExchange exchangeUnmodifiable = new SimpleHttpExchangeUnmodifiable(exchange);
            final Runnable[] supported = new Runnable[]{
                exchange::getURI,
                exchange::getPublicAddress,
                exchange::getLocalAddress,
                exchange::getHttpContext,
                exchange::getHttpPrincipal,
                exchange::getProtocol,
                exchange::getRequestHeaders,
                exchange::getRequestMethod,
                exchange::getRawGet,
                exchange::getGetMap,
                exchange::hasGet,
                exchange::getRawPost,
                exchange::getPostMap,
                exchange::getMultipartFormData,
                exchange::hasPost,
                exchange::getCookies,
                () -> exchange.getAttribute(""),
                exchange::getHttpContext,
                () -> exchange.getHttpContext().getHandler(),
                () -> exchange.getHttpContext().getPath(),
                () -> exchange.getHttpContext().getAttributes(),
                () -> exchange.getHttpContext().getFilters(),
                () -> exchange.getHttpContext().getAuthenticator()
            };

            final Runnable[] unsupported = new Runnable[]{
                exchangeUnmodifiable::getHttpServer,
                exchangeUnmodifiable::getHttpExchange,
                exchangeUnmodifiable::getResponseHeaders,
                exchangeUnmodifiable::getResponseCode,
                () -> exchangeUnmodifiable.setCookie(null),
                () -> exchangeUnmodifiable.setCookie(new SimpleHttpCookie.Builder("k", "v").build()),
                exchangeUnmodifiable::getOutputStream,
                () -> {
                    try{
                        exchangeUnmodifiable.sendResponseHeaders(-1, -1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(-1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new byte[0]);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new byte[0], false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new byte[0], -1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new byte[0], -1, false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send("null");
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send("null", false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send("null", -1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send("null", -1, false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new File(""));
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new File(""), false);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new File(""), -1);
                    }catch(final IOException ignored){ }
                },
                () -> {
                    try{
                        exchangeUnmodifiable.send(new File(""), -1, false);
                    }catch(final IOException ignored){ }
                },
                exchangeUnmodifiable::close,
                () -> exchangeUnmodifiable.setAttribute("k", "v"),
                () -> exchangeUnmodifiable.getHttpContext().getServer(),
                () -> exchangeUnmodifiable.getHttpContext().setHandler(null),
                () -> exchangeUnmodifiable.getHttpContext().setAuthenticator(null)
            };

            for(final Runnable method : supported)
                Assertions.assertDoesNotThrow(method::run, "A supported operation was blocked");
            for(final Runnable method : unsupported)
                Assertions.assertThrows(UnsupportedOperationException.class, method::run, "An unsupported operation ran without permission");

            exchange.send("", 200);
        };

        server.createContext("", handler);
        server.start();

        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port))
            .build();

        final int code = HttpClient.newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::statusCode).get(10, TimeUnit.SECONDS);
        Assertions.assertEquals(200, code, "Test did not receive response from server");
    }

}
