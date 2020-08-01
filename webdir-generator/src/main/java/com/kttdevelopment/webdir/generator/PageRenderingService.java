package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.render.DefaultFrontMatterLoader;
import com.kttdevelopment.webdir.generator.render.PageRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class PageRenderingService {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final PageRenderer render = new PageRenderer();

    private final File source;
    private final File output;

    public PageRenderingService(final File defaults, final File source, final File output) throws IOException{
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger        = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        logger.info(locale.getString("pageRenderer.const"));

        final String dabs = defaults.getAbsolutePath();
        logger.fine(locale.getString("pageRenderer.debug.const.default",dabs));

        defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,source);

        final Path sourcePath = source.getAbsoluteFile().toPath();
        this.source = source;
        this.output = output;

        logger.fine(locale.getString("pageRenderer.debug.const.source",dabs));
        logger.fine(locale.getString("pageRenderer.debug.const.output",dabs));

        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        final boolean clean =
            Vars.Test.clear ||
            (output.exists() &&
             config.getConfig().getBoolean(Vars.Config.cleanKey) &&
             output.getAbsolutePath().startsWith(new File("").getAbsolutePath())
            );

        // render files
        if(clean || Objects.requireNonNullElse(source.list(), new File[0]).length != 0){
            // ^ clean output must run regardless if root folder is empty
            // make sure that only files in the web dir domain will be considered for deletion
            if(clean)
                // Files must be delete recursively because for some reason java doesn't allow deletion of a folder with contents
                try(Stream<Path> walk = Files.walk(output.toPath())){
                    //noinspection ResultOfMethodCallIgnored
                    walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                }catch(final IOException e){
                    logger.warning(locale.getString("pageRenderer.const.failedCleanOutput") + '\n' + Exceptions.getStackTraceAsString(e));
                }
            try{
                // render files
                Files.walk(sourcePath).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    if(render(Paths.get(sourcePath.toString(), sourcePath.relativize(path).toString()).toFile()))
                        rendered.incrementAndGet();
                });
            }catch(final IOException e){
                logger.severe(locale.getString("pageRenderer.const.failedWalk") + '\n' + Exceptions.getStackTraceAsString(e));
                throw e;
            }
        }
        logger.info(locale.getString("pageRenderer.const.loaded",rendered.get(),total.get()));
    }

    // target is the source file
    public final boolean render(final File target){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        final String tabs = target.getAbsolutePath();
        logger.finest(locale.getString("pageRenderer.debug.render",tabs));

        if(target.isDirectory()){
            logger.warning(locale.getString("pageRenderer.render.noRenderDirectory", target));
            return false;
        }

        final Path path = target.getAbsoluteFile().toPath();
        final Path rel  = source.getAbsoluteFile().toPath().relativize(path);
        final Path out  = Paths.get(output.getAbsolutePath(),rel.toString());

        if(!target.exists()){
            try{
                logger.finest(locale.getString("pageRenderer.debug.render.delete", tabs));
                Files.delete(out);
                return true;
            }catch(final IOException e){
                logger.warning(locale.getString("pageRenderer.render.failedDelete", target) + '\n' + Exceptions.getStackTraceAsString(e));
                return false;
            }
        }else{
            final File parent = out.toFile().getParentFile();
            if(parent.exists() || parent.mkdirs())
                try{
                    final byte[] bytes = Files.readAllBytes(path);
                    try{
                        logger.finest(locale.getString("pageRenderer.debug.render.write",out.toFile().getAbsolutePath()));
                        Files.write(out, render.apply(target,defaultFrontMatterLoader.getDefaultFrontMatter(target),bytes));
                        return true;
                    }catch(final IOException e){
                        logger.warning(locale.getString("pageRenderer.render.failedWrite", target) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.warning(locale.getString("pageRenderer.render.writeSecurity", target) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                }catch(final IOException e){
                    logger.warning(locale.getString("pageRenderer.render.failedRead", path) + '\n' + Exceptions.getStackTraceAsString(e));
                }
            else
                logger.finest(locale.getString("pageRenderer.debug.render.missingDir",tabs,parent.getAbsolutePath()));
        }
        return false;
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PageRenderingService")
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("pageRenderer",render)
            .addObject("source",source.getAbsolutePath())
            .addObject("output",output.getAbsolutePath())
            .toString();
    }

}
