package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.renderer.DefaultFrontMatterLoader;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class PageRenderingService {

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;

    private final File defaults, sources, output;

    PageRenderingService(final File defaults, final File sources, final File output){
        final LocaleService locale          = Main.getLocaleService();
        final ConfigService config          = Main.getConfigService();
        final Logger logger                 = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        logger.info(locale.getString("pageRenderer.const.started"));

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);
        this.output   = Objects.requireNonNull(output);

        defaultFrontMatterLoader = new DefaultFrontMatterLoader(defaults,sources);

        // if clean and dir exists and dir is parent of project folder (safety check)
        final boolean clean = output.exists() && config.getConfig().getBoolean("clean") && output.getAbsolutePath().startsWith(Main.getDirectory().getAbsolutePath());
    }

}
