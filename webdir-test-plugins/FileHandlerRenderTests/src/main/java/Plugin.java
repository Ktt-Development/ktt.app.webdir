import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public final class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public final void onEnable(){

        addRenderer("firstFH",new Renderer(){

            @Override
            public final String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
                return "firstFH";
            }

        });

        addRenderer("secondFH",new Renderer(){

            @Override
            public final String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
                return "secondFH";
            }

        });

        addRenderer("exceptionFH",new Renderer(){

            @Override
            public final String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
                throw new RuntimeException();
            }

        });

    }

}
