package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer determines how content will appear.
 *
 * @see com.kttdevelopment.webdir.api.server.ExchangeRendererAdapter
 * @see com.kttdevelopment.webdir.api.server.ExchangeRenderer
 * @see com.kttdevelopment.webdir.api.server.FileRendererAdapter
 * @see com.kttdevelopment.webdir.api.server.FileRenderer
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface Renderer {

    /**
     * Renders content from a file.
     *
     * @param input file in the sources folder
     * @param output file in the output folder
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders, if any)
     * @return rendered content
     *
     * @see ConfigurationSection
     * @since 01.00.00
     */
    String render(final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content);

}
