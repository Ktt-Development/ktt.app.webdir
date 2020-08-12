package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.QuadriFunction;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class PageRenderer implements QuadriFunction<File,File,ConfigurationSection,byte[],byte[]> {

    @Override
    public final byte[] apply(final File IN, final File OUT, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final ILocaleService locale  = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        final String fileABS = IN.getAbsolutePath();
        final String str = new String(bytes);

        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.render", fileABS, defaultFrontMatter, str));

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.frontMatter", fileABS, frontMatter));

        if(!frontMatter.hasFrontMatter() && defaultFrontMatter == null) return bytes; // return raw if both are null
    // create front matter
        final ConfigurationSection mergedFrontMatter = new ConfigurationSectionImpl();
        if(defaultFrontMatter != null)
            mergedFrontMatter.setDefault(defaultFrontMatter);
        if(frontMatter.hasFrontMatter()) // file front matter overrides default
            mergedFrontMatter.setDefault(frontMatter.getFrontMatter());

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(IN,mergedFrontMatter);
    // render page
        final List<String> renderersStr = finalFrontMatter.getList(Vars.Renderer.rendererKey,String.class);

        // if no renderers then return given bytes
        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(Vars.Renderer.rendererKey,renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(frontMatter.getContent());

        renderers.forEach(renderer -> {
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<String> future = executor.submit(() -> {
                final String ct = content.get();
                final String out = renderer.getRenderer().render(IN, OUT, finalFrontMatter, ct);
                logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply", renderer.getRendererName(),fileABS, ct, out));
                return out;
            });

            try{
                content.set(Objects.requireNonNull(future.get(Vars.Plugin.loadTimeout, Vars.Plugin.loadTimeoutUnit)));
            }catch(final TimeoutException | InterruptedException e){
                logger.severe(
                    locale.getString("pageRenderer.pageRenderer.timedOut", renderer.getPluginName(), renderer.getRendererName(), IN.getPath(), Vars.Plugin.loadTimeout + " " + Vars.Plugin.loadTimeoutUnit.name().toLowerCase())
                );
            }catch(final Throwable e){
                logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught", renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }finally{
                future.cancel(true);
                executor.shutdownNow();
            }
        });
        return content.get().getBytes();
    }

}
