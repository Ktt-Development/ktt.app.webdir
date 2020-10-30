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
                return asBytes("1");
            }
        });
        addRenderer("ex", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                throw new RuntimeException();
            }
        });
        addRenderer("timed", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                try{
                    Thread.sleep(60 * 1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                return asBytes("2");
            }
        });
    }

}
