package com.kttdevelopment.webdir.api.formatter;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * Formats server files when they are initially added to the server.
 *
 * @see PostFormatter
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface PreFormatter extends Formatter {

    /**
     * Formats file content.
     *
     * @param source file that is being formatted
     * @param yamlFrontMatter yaml front matter for the file; null if none present
     * @param content file content with a strong
     * @return formatted file
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    String format(final File source, final ConfigurationSection yamlFrontMatter, final String content);

}
