package simplehttpserver;

import com.kttdevelopment.simplehttpserver.*;
import com.kttdevelopment.webdir.server.httpserver.SimpleHttpServerUnmodifiable;
import com.sun.net.httpserver.*;
import org.junit.*;

import java.io.IOException;

public class UnmodifiableServerTests {

    @Test
    public void testSupported() throws IOException{
        SimpleHttpServer server = SimpleHttpServer.create();
        try{
            server.getAddress();
        }catch(final UnsupportedOperationException ignored){
            Assert.fail("Supported operation address was blocked");
        }
        try{
            server.getExecutor();
        }catch(final UnsupportedOperationException ignored){
            Assert.fail("Supported operation executor was blocked");
        }
    }

    @Test
    public void testUnsupported() throws IOException{
        final SimpleHttpServer server = SimpleHttpServer.create();
        final SimpleHttpServer serverUnmodifiable = new SimpleHttpServerUnmodifiable(server);

        final Runnable[] testMethods = new Runnable[]{
            serverUnmodifiable::getHttpServer,
            () -> {
                try{
                    serverUnmodifiable.bind(null);
                }catch(final IOException ignored){
                    Assert.fail("Unmodifiable server was allowed to attempt bind");
                }
            },
            () -> {
                try{
                    serverUnmodifiable.bind(null,-1);
                }catch(final IOException ignored){
                    Assert.fail("Unmodifiable server was allowed to attempt bind");
                }
            },
            () -> serverUnmodifiable.setExecutor(null),
            () -> serverUnmodifiable.setHttpSessionHandler(null),
            serverUnmodifiable::getHttpSessionHandler,
            () -> serverUnmodifiable.getHttpSession((HttpExchange) null),
            () -> serverUnmodifiable.getHttpSession((SimpleHttpExchange) null),
            serverUnmodifiable::start,
            serverUnmodifiable::stop,
            () -> serverUnmodifiable.stop(-1)
        };

        for(final Runnable method : testMethods){
            try{
                method.run();
                Assert.fail("An unsupported operation ran without permission");
            }catch(final UnsupportedOperationException ignored){
            }
        }
    }

}
