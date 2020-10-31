package com.kttdevelopment.webdir.client.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UnmodifiableServerTests {

      @Test
    public void testSupported() throws IOException{
        final SimpleHttpServer serverUnmodifiable = new SimpleHttpServerUnmodifiable(SimpleHttpServer.create());
        Assertions.assertDoesNotThrow(serverUnmodifiable::getAddress, "Supported operation was blocked");
        Assertions.assertDoesNotThrow(serverUnmodifiable::getContexts, "Supported operation was blocked");
    }

    @Test
    public void testUnsupported() throws IOException{
        final SimpleHttpServer serverUnmodifiable = new SimpleHttpServerUnmodifiable(SimpleHttpServer.create());

        final Runnable[] testMethods = new Runnable[]{
            serverUnmodifiable::getHttpServer,
            () -> {
                try{
                    serverUnmodifiable.bind(null);
                }catch(final IOException ignored){
                    Assertions.fail("Unmodifiable server was allowed to attempt bind");
                }
            },
            () -> {
                try{
                    serverUnmodifiable.bind(null,-1);
                }catch(final IOException ignored){
                    Assertions.fail("Unmodifiable server was allowed to attempt bind");
                }
            },
            serverUnmodifiable::getExecutor,
            () -> serverUnmodifiable.setExecutor(null),
            () -> serverUnmodifiable.setHttpSessionHandler(null),
            serverUnmodifiable::getHttpSessionHandler,
            () -> serverUnmodifiable.getHttpSession((HttpExchange) null),
            () -> serverUnmodifiable.getHttpSession((SimpleHttpExchange) null),
            serverUnmodifiable::start,
            serverUnmodifiable::stop,
            () -> serverUnmodifiable.stop(-1)
        };

        for(final Runnable method : testMethods)
            Assertions.assertThrows(UnsupportedOperationException.class, method::run, "Unsupported operation was not blocked");
    }

}
