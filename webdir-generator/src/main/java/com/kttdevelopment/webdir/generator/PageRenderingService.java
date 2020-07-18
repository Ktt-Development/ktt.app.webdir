package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.render.PageRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class PageRenderingService {

    private final BiFunction<File,byte[],byte[]> render = new PageRenderer();

    private final File source;
    private final File output;

    public PageRenderingService(final File source, final File output) throws IOException{
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        logger.info(locale.getString("pageRenderer.const"));

        final Path sourcePath = source.getAbsoluteFile().toPath();
        this.source = source;
        final String outputPath = output.getAbsolutePath();
        this.output = output;

        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        if(source.exists() && Objects.requireNonNullElse(source.list(),new File[0]).length != 0)
            try{
                // make sure that only files in the web dir domain will be considered for deletion
                if(Vars.Test.clear || (output.exists() && config.getConfig().getBoolean(Vars.Config.cleanKey) && output.getAbsolutePath().startsWith(new File("").getAbsolutePath())))
                    // Files must be delete recursively because for some reason java doesn't allow deletion of a folder with contents
                    try(Stream<Path> walk = Files.walk(output.toPath())){
                        //noinspection ResultOfMethodCallIgnored
                        walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    }catch(final IOException e){
                        logger.warning(locale.getString("pageRenderer.rdr.clean") + '\n' + Exceptions.getStackTraceAsString(e));
                    }

                Files.walk(sourcePath).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    if(render(Paths.get(sourcePath.toString(),sourcePath.relativize(path).toString()).toFile()))
                        rendered.incrementAndGet();
                });
            }catch(final IOException e){
                logger.severe(locale.getString("pageRenderer.const.IO") + '\n' + Exceptions.getStackTraceAsString(e));
                throw e;
            }
        logger.info(locale.getString("pageRenderer.const.loaded",rendered.get(),total.get()));
    }

    public final boolean render(final File target){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        if(target.isDirectory()){
            logger.warning(locale.getString("pageRenderer.render.dir", target));
            return false;
        }

        final Path path = target.getAbsoluteFile().toPath();
        final Path rel = source.getAbsoluteFile().toPath().relativize(path);
        final Path out = Paths.get(output.getAbsolutePath(),rel.toString());

        if(!target.exists()){
            if(!out.toFile().delete()){
                logger.warning(locale.getString("pageRenderer.render.delete", target));
                return false;
            }else
                return true;
        }else{
            final File parent = out.toFile().getParentFile();
            if(parent.exists() || parent.mkdirs())
                try{
                    final byte[] bytes = Files.readAllBytes(path);
                    try{
                        Files.write(out, render.apply(target,bytes));
                        return true;
                    }catch(final IOException e){
                        logger.warning(locale.getString("pageRenderer.render.writeIO", target) + '\n' + Exceptions.getStackTraceAsString(e));
                    }catch(final SecurityException e){
                        logger.warning(locale.getString("pageRenderer.render.writeSec", target) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                }catch(final IOException e){
                    logger.warning(locale.getString("pageRenderer.render.readIO", path) + '\n' + Exceptions.getStackTraceAsString(e));
                }
        }
        return false;
    }

}
