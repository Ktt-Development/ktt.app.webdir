package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

public final class PageRenderingService {

    private final File defaults, sources, output;

    PageRenderingService(final File defaults, final File sources, final File output){
        final LocaleService locale  = Main.getLocale();
        final Logger logger         = Main.getLogger(locale.getString("page-render.name"));
        final YamlMapping config    = Main.getConfig();

        // todo: log init

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        // todo: clean (recursive)

        // todo: render
            // todo: defaults

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

        return false;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaults", defaults)
            .addObject("sources", sources)
            .addObject("output", output)
            .toString();
    }

}
