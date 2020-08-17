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
        addRenderer("firstEx",new Renderer(){

            @SuppressWarnings("SpellCheckingInspection")
            @Override
            public String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
                return "DUPLICATEEX";
            }

        });

    }

}
