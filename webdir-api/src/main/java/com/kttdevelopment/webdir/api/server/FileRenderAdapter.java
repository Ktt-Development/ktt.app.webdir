package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;

import java.io.File;

/**
 * A renderer that converts bytes to content.
 *
 * @see Renderer
 * @see SimpleHttpExchange
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface FileRenderAdapter extends Renderer {

    /**
     * Renders content from a file.
     *
     * @param exchange information on client
     * @param source equivalent file in the sources folder
     * @param bytes content of source file in bytes
     * @return rendered content
     *
     * @see SimpleHttpExchange
     * @since 01.00.00
     * @author Ktt Development
     */
    String render(final SimpleHttpExchange exchange, final File source, final byte[] bytes);

}
