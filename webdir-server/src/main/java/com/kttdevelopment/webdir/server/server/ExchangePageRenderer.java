package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.TriFunction;

import java.io.File;

public class ExchangePageRenderer implements TriFunction<File, ConfigurationSection,byte[],byte[]> {

    @Override
    public byte[] apply(final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        return new byte[0];
    }

}
