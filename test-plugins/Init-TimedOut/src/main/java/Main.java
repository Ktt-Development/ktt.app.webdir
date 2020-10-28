import com.kttdevelopment.webdir.api.PluginService;
import com.kttdevelopment.webdir.api.WebDirPlugin;

public class Main extends WebDirPlugin {

    public Main(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        try{
            Thread.sleep(60 * 1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
