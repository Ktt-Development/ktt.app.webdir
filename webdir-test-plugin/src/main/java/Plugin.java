import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

import java.util.concurrent.TimeUnit;

public class Plugin extends WebDirPlugin {

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        try{
            Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
