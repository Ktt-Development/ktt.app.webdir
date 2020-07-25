package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.logging.Logger;

public final class PageRenderer implements BiFunction<File,byte[],byte[]> {

    private final DefaultRenderer defaultRenderer = new DefaultRenderer();

    @Override
    public final byte[] apply(final File file, final byte[] bytes){
        final LocaleService locale  = !Vars.Test.testmode ? Main.getLocaleService() : null;
        final Logger logger         = !Vars.Test.testmode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(!frontMatter.hasFrontMatter()) return bytes;

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(file,frontMatter.getFrontMatter());
        final List<String> renderersStr = finalFrontMatter.getList(Vars.Renderer.rendererKey,String.class);

        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

        content.set(defaultRenderer.apply(file,content.get()));

        renderers.forEach(renderer -> {
            try{
                content.set(renderer.getRenderer().render(file, finalFrontMatter, content.get()));
            }catch(final Throwable e){
                if(!Vars.Test.testmode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.rdr.uncaught",renderer.getPluginName(),renderer.getRendererName(),file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        return content.get().getBytes();
    }

}
