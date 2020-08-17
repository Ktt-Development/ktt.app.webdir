import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public final class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public final void onEnable(){
        throw new RuntimeException();
    }

}
