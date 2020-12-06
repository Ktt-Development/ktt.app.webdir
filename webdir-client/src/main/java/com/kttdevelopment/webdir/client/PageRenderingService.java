package com.kttdevelopment.webdir.client;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.client.renderer.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class PageRenderingService {

    private final LocaleService locale;
    private final Logger logger;

    private final PageRenderer renderer;
    private final File defaults, sources, output;

    PageRenderingService(final File defaults, final File sources, final File output){
        locale = Main.getLocale();
        logger = Main.getLogger(locale.getString("page-renderer.name"));
        final Map<String,Object> config    = Main.getConfig();

        logger.info(locale.getString("page-renderer.constructor.start"));

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        // clean if dir exists, is parent of project (safety check) and clean bool
        try{
            if(Boolean.parseBoolean(config.get(ConfigService.CLEAN).toString()) && output.exists() && output.getCanonicalPath().startsWith(new File(".").getAbsolutePath())){
                try(final Stream<Path> walk = Files.walk(output.toPath())){
                    walk.sorted(Comparator.reverseOrder()) // reverse because inner must be deleted first
                        .forEach(path -> {
                            try{
                                Files.delete(path);
                            }catch(NoSuchFileException ignored){ // OK
                            }catch(final IOException | SecurityException e){
                                logger.severe(locale.getString("page-renderer.constructor." + (e instanceof DirectoryNotEmptyException ? "dir" : "delete"), path.toFile().getAbsolutePath()) + LoggerService.getStackTraceAsString(e));
                            }
                        });
                }catch(final IOException | SecurityException e){
                    logger.severe(locale.getString("page-renderer.constructor.clean", output.getPath()) + LoggerService.getStackTraceAsString(e));
                }
                // fail message was already printed in foreach
            }
        }catch(final IOException e){
            logger.severe(locale.getString("page-renderer.constructor.path", output.getPath()) + LoggerService.getStackTraceAsString(e));
        }

        // render

        final AtomicInteger total    = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        renderer = new PageRenderer(sources, output, new DefaultFrontMatterLoader(defaults, sources, output));

        if(!output.exists() && !output.mkdirs()){
            logger.severe(locale.getString("page-renderer.constructor.output", output.getPath()));
        }else if(sources.exists()){
            try{
                Files.walk(sources.toPath()).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    final File file = path.toFile();
                    final FileRender render = render(file);
                    logger.finer(locale.getString("page-renderer.render.finish", file.getPath()));
                    final byte[] bytes;
                    if(render != null && (bytes = render.getContentAsBytes()) != null && render.getOutputFile() != null)
                        try{
                            Files.write(render.getOutputFile().toPath(), bytes);
                            rendered.incrementAndGet();
                        }catch(final IOException e){
                            logger.warning(locale.getString("page-renderer.render.write", file.getPath()) + LoggerService.getStackTraceAsString(e));
                        }
                    else
                        logger.warning(locale.getString("page-renderer.render.null", file.getPath()));
                });
            }catch(final IOException e){
                logger.severe(locale.getString("page-renderer.constructor.walk", sources.getPath()) + LoggerService.getStackTraceAsString(e));
            }
        }
        logger.info(locale.getString("page-renderer.constructor.finish"));
    }

    public final File getDefaults(){
        return defaults;
    }

    public final File getSources(){
        return sources;
    }

    public final File getOutput(){
        return output;
    }

    //

    public final FileRender render(final File IN){
        return render(IN, null, null);
    }

    public final FileRender render(final File IN, final SimpleHttpServer server, final SimpleHttpExchange exchange){
        logger.finer(locale.getString("page-renderer.render.start", IN.getPath()));
        if(!IN.exists()){
            logger.warning(locale.getString("page-renderer.render.missing", IN.getPath()));
            return null;
        }

        final boolean online = server != null && exchange != null;

        final File rendered;
        try{
            final Path path = IN.getAbsoluteFile().toPath();
            final Path rel  = (!online ? sources : output).getCanonicalFile().toPath().relativize(path);
            rendered        = new File(output, rel.toString()).getCanonicalFile();
        }catch(final IOException e){
            logger.warning(locale.getString("page-renderer.render.missing", IN.getPath()) + '\n' + LoggerService.getStackTraceAsString(e));
            return null;
        }

        final File parent = rendered.getParentFile();
        if(parent.exists() || parent.mkdirs())
            return
                !online
                ? renderer.render(IN, rendered)
                : renderer.render(rendered, null, server, exchange);
        else
            logger.warning(locale.getString("page-renderer.render.parent", IN.getPath(), parent.getPath()));
        return null;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("renderer", renderer)
            .addObject("defaults", defaults)
            .addObject("sources", sources)
            .addObject("output", output)
            .toString();
    }

}
