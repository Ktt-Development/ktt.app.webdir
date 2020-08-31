package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.Main;

import java.io.File;
import java.util.logging.Logger;

public final class PageRenderer {

    private final LocaleService locale;
    private final Logger logger;

    private final DefaultFrontMatterLoader defaultFrontMatterLoader;
    private final File sources, output;

    public PageRenderer(final File sources, final File output, final DefaultFrontMatterLoader defaultFrontMatterLoader){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        this.sources = sources;
        this.output  = output;
        this.defaultFrontMatterLoader = defaultFrontMatterLoader;
    }

    public final byte[] render(final File file){
        final ConfigurationSection defaultFrontMatter = defaultFrontMatterLoader.getDefaultFrontMatter(file);

        // todo: yamlfrontmatter

        return null;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultFrontMatterLoader",defaultFrontMatterLoader)
            .addObject("sources",sources)
            .addObject("output",output)
            .toString();
    }

}
