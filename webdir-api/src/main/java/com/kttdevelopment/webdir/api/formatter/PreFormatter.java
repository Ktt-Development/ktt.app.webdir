package com.kttdevelopment.webdir.api.formatter;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public interface PreFormatter extends Formatter {

    String format(final File source, final ConfigurationSection yamLFrontMatter, final String content);

}
