package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.TriFunction;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class PageRenderer implements TriFunction<File,ConfigurationSection,byte[],byte[]> {

    @Override
    public final byte[] apply(final File file, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final ILocaleService locale  = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        @SuppressWarnings("SpellCheckingInspection")
        final String fabs = file.getAbsolutePath();
        final String str = new String(bytes);

        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.render", fabs, defaultFrontMatter, str));

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.frontMatter", fabs, frontMatter));

        if(!frontMatter.hasFrontMatter() && defaultFrontMatter == null) return bytes; // return raw if both are null
    // create front matter
        final ConfigurationSection mergedFrontMatter = new ConfigurationSectionImpl();
        if(defaultFrontMatter != null)
            mergedFrontMatter.setDefault(defaultFrontMatter);
        if(frontMatter.hasFrontMatter()) // file front matter overrides default
            mergedFrontMatter.setDefault(frontMatter.getFrontMatter());

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(file,mergedFrontMatter);
    // render page
        final List<String> renderersStr = finalFrontMatter.getList(Vars.Renderer.rendererKey,String.class);

        // if no renderers then return given bytes
        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(Vars.Renderer.rendererKey,renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

        renderers.forEach(renderer -> {
            final String ct = content.get();
            try{
                content.set(renderer.getRenderer().render(file, finalFrontMatter, ct));
                logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply", fabs, renderer, ct, content.get()));
            }catch(final Throwable e){
                logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught", renderer.getPluginName(), renderer.getRendererName(), file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        return content.get().getBytes();
    }

}
