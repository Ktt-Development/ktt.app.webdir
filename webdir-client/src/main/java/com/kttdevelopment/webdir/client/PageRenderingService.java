package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.client.renderer.DefaultFrontMatterLoader;
import com.kttdevelopment.webdir.client.renderer.PageRenderer;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class PageRenderingService {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final PageRenderer renderer;

    private final File defaults, sources, output;

    private final LocaleService locale;
    private final Logger logger;

    PageRenderingService(final File defaults, final File sources, final File output){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        final ConfigService config  = Main.getConfigService();

        logger.info(locale.getString("pageRenderer.const.started"));

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        // if clean and dir exists and dir is parent of project folder (safety check)
        final boolean clean = output.exists() && config.getConfig().getBoolean("clean") && output.getAbsolutePath().startsWith(Main.directory.getAbsolutePath());

        if(clean){
            final AtomicBoolean failedDelete = new AtomicBoolean(false);
            // files must be deleted recursively because Java doesn't allow deletion of populated folders
            try(final Stream<Path> walk = Files.walk(output.toPath())){
                walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try{
                            Files.delete(path);
                        }catch(final IOException e){
                            failedDelete.set(true);
                            logger.warning(locale.getString("pageRenderer.const.failedDelete", path) + '\n' + LoggerService.getStackTraceAsString(e));
                        }
                    });
            }catch(final IOException e){
                logger.warning(locale.getString("pageRenderer.const.failedClean",output) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            if(failedDelete.get())
                logger.warning(locale.getString("pageRenderer.const.failedClean",output));
        }
        // render
        defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,sources);
        renderer = new PageRenderer(sources,output,defaultFrontMatterLoader);

        final AtomicInteger total    = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        if(!output.exists() && !output.mkdirs()){
            logger.severe(locale.getString("pageRenderer.const.failedCreate",output));
        }else{
             try{
                Files.walk(sources.toPath()).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    if(render(path.toFile()))
                        rendered.incrementAndGet();
                });
            }catch(final IOException e){
                logger.warning(locale.getString("pageRenderer.const.failedWalk",sources) + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }
        logger.info(locale.getString("pageRenderer.const.finished",rendered.get(),total.get()));
    }

    public final boolean render(final File in){
        logger.finest(locale.getString("pageRenderer.renderer",in));
        if(!in.exists()){
            logger.warning(locale.getString("pageRenderer.render.fileNotFound", in));
            return false;
        }

        final Path path      = in.getAbsoluteFile().toPath();
        final Path rel       = sources.getAbsoluteFile().toPath().relativize(path);
        final Path rendered  = Paths.get(output.getAbsolutePath(), rel.toString());

        try{
            final File parent = rendered.toFile().getParentFile();
            if(parent.exists() || parent.mkdirs()){
                Files.write(rendered, renderer.render(in,rendered.toFile()));
                logger.finest(locale.getString("pageRenderer.render.rendered",in,rendered.toFile()));
                return true;
            }else{
                logger.severe(locale.getString("pageRenderer.render.parentNotFound", in, parent));
            }
        }catch(final SecurityException e){
            logger.warning(locale.getString("pageRenderer.const.accessDenied",in) + '\n' + LoggerService.getStackTraceAsString(e));
        }catch(final IOException e){
            logger.warning(locale.getString("pageRenderer.const.failedWrite",in) + '\n' + LoggerService.getStackTraceAsString(e));
        }
        return false;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("renderer",renderer)
            .addObject("defaults",defaults)
            .addObject("sources",sources)
            .addObject("output",output)
            .toString();
    }

}
