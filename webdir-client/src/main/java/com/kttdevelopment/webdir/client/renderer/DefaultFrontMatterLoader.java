package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.webdir.client.LocaleService;
import com.kttdevelopment.webdir.client.Main;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class DefaultFrontMatterLoader {

    public DefaultFrontMatterLoader(final File defaults){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger        = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        for(final File file : Objects.requireNonNullElse(defaults.listFiles(File::isFile),new File[0])){
            
        }
    }

}
