import com.kttdevelopment.webdir.api.*;

public class Main extends WebDirPlugin {

    public Main(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){
        addRenderer("1", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("2");
            }
        });
        addRenderer("2", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("2");
            }
        });
        addRenderer("3", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("3");
            }
        });
        addRenderer("copy", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return render.getContentAsBytes();
            }
        });
    }

}
