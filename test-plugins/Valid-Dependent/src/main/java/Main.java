import com.kttdevelopment.webdir.api.*;

import java.io.File;

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
        addRenderer("exchange", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("exchange");
            }
        });
        addRenderer("perm", new Renderer("perm"){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("perm");
            }
        });
        addRenderer("perm2", new Renderer("perm2"){
            @Override
            public byte[] render(final FileRender render){
                return asBytes("perm2");
            }
        });
        addRenderer("false", new Renderer(){
            @Override
            public boolean test(final File file){
                return false;
            }

            @Override
            public byte[] render(final FileRender render){
                return asBytes("true");
            }
        });
    }

}
