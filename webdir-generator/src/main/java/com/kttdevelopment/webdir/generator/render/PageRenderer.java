package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.logging.Logger;

public final class PageRenderer implements BiFunction<File,byte[],byte[]> {

    @Override
    public final byte[] apply(final File file, final byte[] bytes){
        final LocaleService locale = !Main.testMode ? Main.getLocaleService() : null;
        final Logger logger = !Main.testMode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(!frontMatter.hasFrontMatter()) return bytes;

        ConfigurationSection tFrontMatter = frontMatter.getFrontMatter();
        tFrontMatter = YamlFrontMatter.loadImports(tFrontMatter);
        tFrontMatter = YamlFrontMatter.loadRelativeImports(file,tFrontMatter);
        final ConfigurationSection finalFrontMatter = tFrontMatter;
        final List<String> renderersStr = finalFrontMatter.getList("renderer",String.class);

        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());
        renderers.forEach(renderer -> {
            try{
                content.set(renderer.getRenderer().format(file, finalFrontMatter, content.get()));
            }catch(final Exception e){
                if(!Main.testMode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.rdr.uncaught",renderer.getPluginName(),renderer.getRendererName(),file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        return content.get().getBytes();
    }

}
