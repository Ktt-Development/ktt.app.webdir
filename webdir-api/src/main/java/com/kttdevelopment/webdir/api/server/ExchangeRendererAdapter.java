package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer that determines how content will appear based on an exchange.
 *
 * @see Renderer
 * @see ExchangeRenderer
 * @see SimpleHttpExchange
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface ExchangeRendererAdapter extends Renderer {

    /**
     * Renders content from a file.
     *
     * @param exchange information on the client
     * @param input file in the sources folder
     * @param output file in the output folder
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders, if any)
     * @return rendered content
     *
     * @see SimpleHttpExchange
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    String render(final SimpleHttpExchange exchange, final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content);

}
