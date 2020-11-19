package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.plugin.PluginRendererEntry;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PageRenderer {

    public static final String
        RENDERERS           = "renderers",
        EXCHANGE_RENDERERS  = "exchange_renderers",
        PLUGIN              = "plugin",
        RENDERER            = "renderer",
        IGNORE              = "ignore",
        IMPORT              = "import",
        IMPORT_RELATIVE     = "import_relative";

    private final LocaleService locale;
    private final Logger logger;

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final File sources, output;

    public PageRenderer(final File source, final File output, final DefaultFrontMatterLoader defaultFrontMatterLoader){
        this.locale = Main.getLocale();
        this.logger = Main.getLogger(locale.getString("page-renderer.name"));

        this.sources = source;
        this.output  = output;
        this.defaultFrontMatterLoader = defaultFrontMatterLoader;
    }

    public final FileRender render(final File IN, final File OUT){
        return render(IN, OUT, null, null);
    }

    public final FileRender render(final File IN, final File OUT, final SimpleHttpServer server, final SimpleHttpExchange exchange){
        final boolean online = server != null && exchange != null;

        byte[] bytes = null;
        if(!IN.isDirectory() && IN.exists()) // only try to load bytes if file and exists
            try{
                bytes = Files.readAllBytes(IN.toPath());
            }catch(final OutOfMemoryError | SecurityException | IOException e){ // if failed read then renders can not be expected to work
                logger.severe(locale.getString("page-renderer.renderer.read", IN.getPath()) + LoggerService.getStackTraceAsString(e));
                return null;
            }

        // front matter
        final Map<String,? super Object> merged = new HashMap<>();
        {
             final Map<String,? super Object> defaultFrontMatter = defaultFrontMatterLoader.getDefaultFrontMatter(IN);
             if(defaultFrontMatter != null)
                merged.putAll(defaultFrontMatter);
        }
        if(!online && bytes != null){ // online & null bytes will not have front matter
            final YamlFrontMatter frontMatter = new YamlFrontMatter(new String(bytes, StandardCharsets.UTF_8));
            if(frontMatter.getFrontMatter() != null){
                 bytes = frontMatter.getContent().getBytes(StandardCharsets.UTF_8);
                 merged.putAll(frontMatter.getFrontMatter());
            }
        }

        final Map<String,? super Object> finalFrontMatter = YamlFrontMatter.loadImports(IN, merged); // do not make immutable, variables are shared across renders
        logger.finest(locale.getString("page-renderer.front-matter.finished", IN.getPath(), finalFrontMatter));

        // render
        final Object r = Objects.requireNonNullElse(finalFrontMatter.get(!online ? RENDERERS : EXCHANGE_RENDERERS), new ArrayList<>());
        final List<?> renderersStr = r instanceof List ? (List<?>) r : List.of(r);

        finalFrontMatter.remove(PageRenderer.IMPORT);
        finalFrontMatter.remove(PageRenderer.IMPORT_RELATIVE);
        finalFrontMatter.remove(PageRenderer.RENDERERS);
        finalFrontMatter.remove(PageRenderer.EXCHANGE_RENDERERS);

        final FileRenderImpl fileRender = new FileRenderImpl(IN, OUT, finalFrontMatter, bytes, server, exchange);

        final boolean ignore = Boolean.parseBoolean(finalFrontMatter.getOrDefault(IGNORE, "false").toString());
        if(ignore) fileRender.setOutputFile(null); // set here so isEmpty returns null output
        {
            if(renderersStr.isEmpty())
                return fileRender;

            final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(renderersStr);

            for(final PluginRendererEntry entry : renderers){
                final Renderer renderer = entry.getRenderer();

                if(ExceptionUtility.requireNonExceptionElse(() -> !renderer.test(IN), false))
                    continue;

                final ExecutorService executor = Executors.newSingleThreadExecutor();

                final String pluginName   = entry.getPluginName();
                final String rendererName = entry.getRendererName();

                // do not use renderer if has no permission
                if(online && !Main.getPermissions().hasPermission(exchange.getPublicAddress().getAddress(), renderer.getPermission()))
                    continue;

                logger.finest(locale.getString("page-renderer.renderer.apply", pluginName, rendererName, IN.getPath(), out(OUT)));

                final Future<byte[]> future = executor.submit(() -> {
                    try{
                        return renderer.render(fileRender);
                    }catch(final Throwable e){
                        logger.severe(locale.getString("page-renderer.renderer.exception", pluginName, rendererName, IN.getPath(), out(OUT)) + LoggerService.getStackTraceAsString(e));
                    }
                    return fileRender.getContentAsBytes();
                });

                try{
                    fileRender.setBytes(future.get(30,TimeUnit.SECONDS));
                }catch(final TimeoutException | InterruptedException e){
                    logger.severe(locale.getString("page-renderer.renderer.time", pluginName, rendererName, IN.getPath(), out(OUT)) + LoggerService.getStackTraceAsString(e));
                }catch(final Throwable e){
                    logger.severe(locale.getString("page-renderer.renderer.exception", pluginName, rendererName, IN.getPath(), out(OUT)) + LoggerService.getStackTraceAsString(e));
                }finally{
                    future.cancel(true);
                    executor.shutdownNow();
                }
            }
        }
        if(ignore) fileRender.setOutputFile(null); // set here so renderers can't override ignore

        return fileRender;
    }

    private String out(final File out){
        return out == null ? "null" : out.getPath();
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("sources", sources)
            .addObject("output", output)
            .addObject("defaultFrontMatterLoader", defaultFrontMatterLoader)
            .toString();
    }

}
