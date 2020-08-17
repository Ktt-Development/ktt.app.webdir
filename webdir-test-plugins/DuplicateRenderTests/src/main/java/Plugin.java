import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public final class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public final void onEnable(){

        addRenderer("first",new Renderer(){

            @Override
            public final String render(final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
                return "DUPLICATE";
            }

        });

    }

}
