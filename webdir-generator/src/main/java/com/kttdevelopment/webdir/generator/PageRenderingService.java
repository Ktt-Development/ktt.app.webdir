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

    private static final String cleanKey = "clean";

    public PageRenderingService(final File source, final File output) throws IOException{
        final LocaleService locale = Main.getLocaleService();
        final ConfigService config = Main.getConfigService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));
        logger.info(locale.getString("pageRenderer.const"));

        final Path sourcePath = source.getAbsoluteFile().toPath();
        final String outputPath = output.getAbsolutePath();

        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        if(source.exists() && Objects.requireNonNullElse(source.list(),new File[0]).length != 0)
            try{
                // make sure that only files in the web dir domain will be considered for deletion
                if(output.exists() && config.getConfig().getBoolean(cleanKey) && output.getAbsolutePath().startsWith(new File("").getAbsolutePath()))
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
                    try{
                        final byte[] bytes = Files.readAllBytes(path);

                        final Path rel = sourcePath.relativize(path);
                        final Path target = Paths.get(outputPath,rel.toString());
                        final File parent = target.toFile().getParentFile();

                        if(parent.exists() || parent.mkdirs())
                            try{
                                Files.write(target, render.apply(target.toFile(),bytes));
                                rendered.incrementAndGet();
                            }catch(final IOException e){
                                logger.warning(locale.getString("pageRenderer.const.writeIO", target) + '\n' + Exceptions.getStackTraceAsString(e));
                            }catch(final SecurityException e){
                                logger.warning(locale.getString("pageRenderer.const.writeSec", target) + '\n' + Exceptions.getStackTraceAsString(e));
                            }
                    }catch(final IOException e){
                        logger.warning(locale.getString("pageRenderer.const.readIO",path) + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                });
            }catch(final IOException e){
                logger.severe(locale.getString("pageRenderer.const.IO") + '\n' + Exceptions.getStackTraceAsString(e));
                throw e;
            }
        logger.info(locale.getString("pageRenderer.const.loaded",rendered.get(),total.get()));
    }

}
