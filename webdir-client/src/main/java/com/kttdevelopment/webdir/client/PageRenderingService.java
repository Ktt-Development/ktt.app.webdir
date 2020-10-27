package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.client.renderer.PageRenderer;
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

    private final PageRenderer renderer;
    private final File defaults, sources, output;

    PageRenderingService(final File defaults, final File sources, final File output){
        final LocaleService locale  = Main.getLocale();
        final Logger logger         = Main.getLogger(locale.getString("page-render.name"));
        final YamlMapping config    = Main.getConfig();

        // todo: log init

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        this.renderer = null; // todo

        // clean if dir exists, is parent of project (safety check) and clean bool
        if(Boolean.parseBoolean(config.string(ConfigService.CLEAN)) && output.exists() && output.getAbsolutePath().startsWith(new File(".").getAbsolutePath())){
            final AtomicBoolean failed = new AtomicBoolean(false);
            try(final Stream<Path> walk = Files.walk(output.toPath())){
                walk.sorted(Comparator.reverseOrder()) // reverse because inner must be deleted first
                    .forEach(path -> {
                        try{
                            Files.delete(path);
                        }catch(NoSuchFileException ignored){ // OK
                        }catch(final IOException | SecurityException e){
                            failed.set(true);
                            // todo: instanceof dirnotempty
                        }
                    });

            }catch(final IOException | SecurityException e){
                // todo: log failed clean
            }
        }

        // render

        final AtomicInteger total = new AtomicInteger(0);
        final AtomicInteger rendered = new AtomicInteger(0);

        if(!output.exists() && !output.mkdirs()){
            // todo: log failure
        }else{
            try{
                Files.walk(sources.toPath()).filter(path -> path.toFile().isFile()).forEach(path -> {
                    total.incrementAndGet();
                    if(render(path.toFile()))
                        rendered.incrementAndGet();
                });
            }catch(final IOException e){
                // todo: log failure
            }
        }
        // todo: log fin
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

    // todo: render method
    public final boolean render(final File IN){
        // todo: log render
        if(!IN.exists()){
            // todo: log missing
            return false;
        }

        final Path path      = IN.getAbsoluteFile().toPath();
        final Path rel       = sources.getAbsoluteFile().toPath().relativize(path);
        final Path rendered  = Paths.get(output.getAbsolutePath(), rel.toString());

        // todo: render + create req dirs
        try{
            final File parent = rendered.toFile().getParentFile();
            if(parent.exists() || parent.mkdirs()){
                final byte[] bytes = null; // todo: render method
                if(bytes != null)
                    Files.write(rendered, bytes);
                else
                    ; // todo: log null
                // todo: log render
                return true;
            }else{
                // todo: log no parent + failed create
            }
        }catch(final IOException | SecurityException e){
            // todo: log err
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
