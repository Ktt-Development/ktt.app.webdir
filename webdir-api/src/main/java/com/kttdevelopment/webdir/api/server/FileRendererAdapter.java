package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer that converts bytes to content.
 *
 * @see Renderer
 * @see FileRenderer
 * @see SimpleHttpExchange
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface FileRendererAdapter extends Renderer {

    /**
     * Renders content from a file.
     *
     * @param exchange information on client
     * @param source file being read
     * @param defaultFrontMatter default yaml front matter configuration
     * @param bytes content of source file in bytes
     * @return rendered content
     *
     * @see SimpleHttpExchange
     * @since 01.00.00
     * @author Ktt Development
     */
    String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes);

}
