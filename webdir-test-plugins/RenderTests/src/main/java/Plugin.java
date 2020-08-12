import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("first", (input, output, yamlFrontMatter, content) -> "first");
        addRenderer("second", (input, output, yamlFrontMatter, content) -> "second");
        addRenderer("exception", (input, output, yamlFrontMatter, content) -> {
            throw new RuntimeException();
        });
    }

}
