package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer determines how content will appear.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface Renderer {

    /**
     * Renders content from a file.
     *
     * @param output where the file will be saved
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders, if any)
     * @return rendered content
     *
     * @see ConfigurationSection
     * @since 01.00.00
     */
    String render(final File output, final ConfigurationSection yamlFrontMatter, final String content);

}
