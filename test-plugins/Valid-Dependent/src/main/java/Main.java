import com.kttdevelopment.webdir.api.*;

public class Main extends WebDirPlugin {

    public Main(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("2", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("1");
            }
        });
    }

}
