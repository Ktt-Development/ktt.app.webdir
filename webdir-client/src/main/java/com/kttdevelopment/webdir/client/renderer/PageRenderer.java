package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.client.plugins.PluginRendererEntry;
import com.kttdevelopment.webdir.client.renderer.yaml.YamlFrontMatter;
import com.kttdevelopment.webdir.client.renderer.yaml.YamlFrontMatterReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class PageRenderer {

    private final LocaleService locale;
    private final Logger logger;

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final File sources, output;

    public PageRenderer(final File sources, final File output, final DefaultFrontMatterLoader defaultFrontMatterLoader){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        this.sources = sources;
        this.output  = output;
        this.defaultFrontMatterLoader = defaultFrontMatterLoader;
    }

    public final byte[] render(final File IN, final File OUT){
        return render(IN,OUT,null);
    }

    @SuppressWarnings("unchecked")
    public final byte[] render(final File IN, final File OUT, final SimpleHttpExchange exchange){
        final AtomicReference<byte[]> bytes = new AtomicReference<>();
        try{
            bytes.set(Files.readAllBytes(IN.toPath()));
        }catch(final IOException e){
            logger.severe(locale.getString("pageRenderer.render.failedRead",IN) + '\n' + LoggerService.getStackTraceAsString(e));
            return null;
        }catch(final OutOfMemoryError e){
            logger.severe(locale.getString("pageRenderer.render.outOfMemory",IN) + '\n' + LoggerService.getStackTraceAsString(e));
            return null;
        }catch(final SecurityException e){
            logger.severe(locale.getString("pageRenderer.render.accessDenied",IN) + '\n' + LoggerService.getStackTraceAsString(e));
            return null;
        }

        // front matter
        logger.finest(locale.getString("pageRenderer.render.applyFrontMatter",IN));
        final ConfigurationSection defaultFrontMatter = defaultFrontMatterLoader.getDefaultFrontMatter(IN);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(new String(bytes.get(),StandardCharsets.UTF_8)).read();

        // merge
        final Map<?,?> merged = new HashMap<>();
        if(defaultFrontMatter != null)
            merged.putAll(defaultFrontMatter.toMapWithDefaults());
        if(frontMatter.hasFrontMatter())
            merged.putAll(frontMatter.getFrontMatter().toMapWithDefaults());
        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(IN,new ConfigurationSectionImpl(merged));
        logger.fine(locale.getString("pageRenderer.render.frontMatter",IN) + '\n' + frontMatter);
        // render
        {
            final List<String> renderersStr = finalFrontMatter.getList("renderers", new ArrayList<>());

            if(renderersStr.isEmpty())
                return bytes.get();

            final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

            // todo: server reference
            for(final PluginRendererEntry entry : renderers){
                final Renderer renderer         = entry.getRenderer();
                final ExecutorService executor  = Executors.newSingleThreadExecutor();

                final String pluginName = entry.getPluginName(), rendererName = entry.getRendererName();

                final Future<byte[]> future = executor.submit(() -> {
                    final AtomicReference<byte[]> buffer = new AtomicReference<>(bytes.get());
                    logger.finest(locale.getString("pageRenderer.render.apply",pluginName,rendererName,IN));
                    // server is null for first page render so null check is ok
                    try{
                        buffer.set(renderer.render(new FileRenderImpl(IN,OUT,finalFrontMatter,bytes.get(),/* todo: server */null,exchange)));
                    }catch(final Throwable e){
                        logger.finest(locale.getString("pageRenderer.render.exception",pluginName,rendererName,IN) + '\n' + LoggerService.getStackTraceAsString(e));
                    }
                    return buffer.get();
                });

                try{
                    bytes.set(future.get(30,TimeUnit.SECONDS));
                }catch(final TimeoutException | InterruptedException e){
                    logger.severe(locale.getString("pageRenderer.render.timedOut",pluginName,rendererName,30 + " " + TimeUnit.SECONDS.name().toLowerCase()) + '\n' + LoggerService.getStackTraceAsString(e));
                }catch(final Throwable e){
                    logger.finest(locale.getString("pageRenderer.render.exception",entry.getPluginName(),entry.getRendererName(),IN) + '\n' + LoggerService.getStackTraceAsString(e));
                }finally{
                    future.cancel(true);
                    executor.shutdownNow();
                }
            }
        }
        return bytes.get();
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("sources",sources)
            .addObject("output",output)
            .toString();
    }

}
