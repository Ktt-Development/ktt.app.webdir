package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
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

    private final File defaults, source, output;

    public PageRenderingService(final File defaults, final File source, final File output) throws IOException{
        this.defaults = defaults;
        this.source   = source;
        this.output   = output;

        final ILocaleService locale = Vars.Main.getLocaleService();
        final ConfigService config  = Vars.Main.getConfigService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        logger.info(locale.getString("pageRenderer.const"));

        final String defABS = defaults.getAbsolutePath();
        logger.fine(locale.getString("pageRenderer.debug.const.default",defABS));

        defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,source);

        final Path sourcePath = source.getAbsoluteFile().toPath();

        logger.fine(locale.getString("pageRenderer.debug.const.source",defABS));
        logger.fine(locale.getString("pageRenderer.debug.const.output",defABS));

        final AtomicInteger total    = new AtomicInteger(0);
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
    public final boolean render(final File IN){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        final String targetABS      = IN.getAbsolutePath();
        logger.finest(locale.getString("pageRenderer.debug.render",targetABS));

        if(IN.isDirectory()){
            logger.warning(locale.getString("pageRenderer.render.noRenderDirectory", IN));
            return false;
        }

        final Path path = IN.getAbsoluteFile().toPath();
        final Path rel  = this.source.getAbsoluteFile().toPath().relativize(path);
        final File OUT  = Paths.get(this.output.getAbsolutePath(), rel.toString()).toFile();

        if(!IN.exists() && OUT.exists()){
            try{
                logger.finest(locale.getString("pageRenderer.debug.render.delete", targetABS));
                Files.delete(OUT.toPath());
                return true;
            }catch(final IOException e){
                if(e instanceof NoSuchFileException) return true;
                logger.warning(locale.getString("pageRenderer.render.failedDelete", IN) + '\n' + Exceptions.getStackTraceAsString(e));
                return false;
            }
        }else if(IN.exists()){
            final File parent = OUT.getParentFile();
            if(parent.exists() || parent.mkdirs())
                try{
                    final byte[] bytes = Files.readAllBytes(path);
                    try{
                        logger.finest(locale.getString("pageRenderer.debug.render.write",OUT.getAbsolutePath()));
                        Files.write(OUT.toPath(), render.apply(IN,OUT,defaultFrontMatterLoader.getDefaultFrontMatter(IN),bytes));
                        return true;
                    }catch(final IOException e){
                        logger.warning(locale.getString("pageRenderer.render.failedWrite", IN) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.warning(locale.getString("pageRenderer.render.writeSecurity", IN) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                }catch(final IOException e){
                    logger.warning(locale.getString("pageRenderer.render.failedRead", path) + '\n' + Exceptions.getStackTraceAsString(e));
                }
            else
                logger.finest(locale.getString("pageRenderer.debug.render.missingDir",targetABS,parent.getAbsolutePath()));
        }
        return false;
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PageRenderingService")
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("pageRenderer",render)
            .addObject("defaults",defaults.getAbsolutePath())
            .addObject("source",source.getAbsolutePath())
            .addObject("output",output.getAbsolutePath())
            .toString();
    }

}
