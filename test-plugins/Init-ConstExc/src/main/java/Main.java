import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Main extends WebDirPlugin {

    public Main(final PluginService service){
        super(service);
        throw new RuntimeException();
    }

}
