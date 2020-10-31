package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.api.FileRender;
import com.kttdevelopment.webdir.client.renderer.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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
        final YamlMapping config    = Main.getConfig();

        logger.info(locale.getString("page-renderer.constructor.start"));

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        // clean if dir exists, is parent of project (safety check) and clean bool
        if(Boolean.parseBoolean(config.string(ConfigService.CLEAN)) && output.exists() && output.getAbsolutePath().startsWith(new File(".").getAbsolutePath())){
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

        // render

        final AtomicInteger total    = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        renderer = new PageRenderer(sources, output, new DefaultFrontMatterLoader(defaults, sources));

        if(!output.exists() && !output.mkdirs()){
            logger.severe(locale.getString("page-renderer.constructor.output", output.getPath()));
        }else{
            try{
                Files.walk(sources.toPath()).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    if(render(path.toFile()))
                        rendered.incrementAndGet();
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

    public final boolean render(final File IN){
        logger.finer(locale.getString("page-renderer.render.start", IN.getPath()));
        if(!IN.exists()){
            logger.warning(locale.getString("page-renderer.render.missing", IN.getPath()));
            return false;
        }

        final Path path      = IN.getAbsoluteFile().toPath();
        final Path rel       = sources.getAbsoluteFile().toPath().relativize(path);
        final Path rendered  = Paths.get(output.getAbsolutePath(), rel.toString());

        try{
            final File parent = rendered.toFile().getParentFile();
            if(parent.exists() || parent.mkdirs()){
                final FileRender output = renderer.render(IN, rendered.toFile());
                final byte[] bytes = output.getContentAsBytes();
                if(bytes != null && output.getOutputFile() != null)
                    Files.write(output.getOutputFile().toPath(), bytes);
                else
                    logger.warning(locale.getString("page-renderer.render.null", IN.getPath()));
                logger.finer(locale.getString("page-renderer.render.finish", IN.getPath()));
                return true;
            }else{
                logger.warning(locale.getString("page-renderer.render.parent", IN.getPath(), parent.getPath()));
            }
        }catch(final IOException | SecurityException e){
            logger.warning(locale.getString("page-renderer.render.write", IN.getPath()) + LoggerService.getStackTraceAsString(e));
        }
        return false;
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
