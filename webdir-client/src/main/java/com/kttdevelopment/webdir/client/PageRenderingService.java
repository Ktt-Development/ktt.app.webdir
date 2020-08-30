package com.kttdevelopment.webdir.client;

import java.io.File;
import java.util.logging.Logger;

public final class PageRenderingService {

    private final File defaults, sources, output;

    PageRenderingService(final File defaults, final File sources, final File output){
        final LocaleService locale          = Main.getLocaleService();
        final Logger logger                 = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        logger.info(locale.getString("pluginLoader.const.started"));

        this.defaults = defaults;
        this.sources  = sources;
        this.output   = output;
    }

}
