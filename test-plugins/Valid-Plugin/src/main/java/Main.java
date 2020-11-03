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
        addRenderer("copy", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return render.getContentAsBytes();
            }
        });
        addRenderer("set", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                render.getFrontMatter().put("set", "1");
                return super.render(render);
            }
        });
        addRenderer("get", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                return asBytes(render.getFrontMatter().get("set").toString());
            }
        });
        addRenderer("out", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                render.setOutputFile(new File(render.getOutputFile().getParentFile(), "output.html"));
                return super.render(render);
            }
        });
        addRenderer("null", new Renderer(){
            @Override
            public byte[] render(final FileRender render){
                render.setOutputFile(null);
                return super.render(render);
            }
        });
    }

}
