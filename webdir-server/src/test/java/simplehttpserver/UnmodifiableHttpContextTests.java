package simplehttpserver;

import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.server.httpserver.SimpleHttpServerUnmodifiable;
import com.sun.net.httpserver.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class UnmodifiableHttpContextTests {

    @Test
    public void testValid() throws IOException{
        final SimpleHttpServer server = SimpleHttpServer.create();
        final SimpleHttpServer unmod = new SimpleHttpServerUnmodifiable(server);

        final String testContext = "testContext";
        final HttpContext context = unmod.createContext(testContext);

        final Runnable[] validMethods = new Runnable[]{
            context::getHandler,
            () -> context.setHandler(HttpExchange::close),
            context::getPath,
            context::getFilters,
            () -> context.setAuthenticator(new Authenticator() {
                @Override
                public Result authenticate(final HttpExchange exch){
                    return null;
                }
            }),
            context::getAuthenticator
        };

        for(final Runnable method : validMethods){
            try{
                method.run();
            }catch(final UnsupportedOperationException ignored){
                Assert.fail("Supported operation was blocked");
            }
        }
    }

    @Test
    public void testInvalid() throws IOException{
        final SimpleHttpServer server = SimpleHttpServer.create();
        final SimpleHttpServer unmod = new SimpleHttpServerUnmodifiable(server);

        final String testContext = "testContext";
        final HttpContext context = unmod.createContext(testContext);

        try{
            context.getServer();
            Assert.fail("Unmodifiable server should not be able to get server from http context");
        }catch(final UnsupportedOperationException ignored){ }
    }

}
