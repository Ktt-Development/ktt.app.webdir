import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("first", (source, yamlFrontMatter, content) -> "first");
        addRenderer("second", (source, yamlFrontMatter, content) -> "second");
        addRenderer("exception", (source, yamlFrontMatter, content) -> {
            throw new RuntimeException();
        });
    }

}
