package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.TriFunction;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class PageRenderer implements TriFunction<File,ConfigurationSection,byte[],byte[]> {

    @Override
    public final byte[] apply(final File file, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final LocaleService locale  = !Vars.Test.testmode ? Main.getLocaleService() : null;
        final Logger logger         = !Vars.Test.testmode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(!frontMatter.hasFrontMatter() && defaultFrontMatter == null) return bytes; // return raw if both are null

        final ConfigurationSection masterFrontMatter = new ConfigurationSectionImpl();
        if(defaultFrontMatter != null)
            masterFrontMatter.setDefault(defaultFrontMatter);
        if(frontMatter.hasFrontMatter()) // file front matter overrides default
            masterFrontMatter.setDefault(frontMatter.getFrontMatter());

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(file,masterFrontMatter);
        final List<String> renderersStr = finalFrontMatter.getList(Vars.Renderer.rendererKey,String.class);

        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

        renderers.forEach(renderer -> {
            try{
                content.set(renderer.getRenderer().render(file, finalFrontMatter, content.get()));
            }catch(final Throwable e){
                if(!Vars.Test.testmode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught", renderer.getPluginName(), renderer.getRendererName(), file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        return content.get().getBytes();
    }

}
