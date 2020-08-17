package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.SimpleHttpServer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer determines how content will appear.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
@SuppressWarnings("SpellCheckingInspection")
public class Renderer {

    private final String permission;

    /**
     * Creates a renderer with no permission.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public Renderer(){
        this.permission = null;
    }

    /**
     * Creates a renderer with a required permission.
     *
     * @param permission permission required to use handler
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public Renderer(final String permission){
        this.permission = permission;
    }

    //

    /**
     * Returns the permission required to use the renderer or null if no permission is required.
     *
     * @return required permission
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public final String getPermission(){
        return permission;
    }

    //

    /**
     * Renders content from a file. <br>
     *
     * <b>Scope:</b> All files. <br>
     * <b>Execution:</b> <code>renderers</code>, <code>exchangeRenderers</code>.
     *
     * @param input file in the sources folder
     * @param output file in the output folder
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders)
     * @return rendered content
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public String render(final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    /**
     * Renders content from a file. <br>
     *
     * <b>Scope:</b> Server files only. <br>
     * <b>Execution:</b> <code>renderers</code>, <code>exchangeRenderers</code>.
     *
     * @param server webdir server
     * @param input file in the sources folder
     * @param output file in the output folder
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders)
     * @return rendered content
     *
     * @see SimpleHttpServer
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public String render(final SimpleHttpServer server, final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    /**
     * Renders content from a file. <br>
     *
     * <b>Scope:</b> Server files only. <br>
     * <b>Execution:</b> <code>exchangeRenderers</code>.
     *
     * @param server webdir server
     * @param exchange http exchange
     * @param input file in the sources folder
     * @param output file in the output folder
     * @param yamlFrontMatter yaml front matter configuration
     * @param content current file content (after previous renders)
     * @return rendered content
     *
     * @see SimpleHttpServer
     * @see SimpleHttpExchange
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File input, final File output, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    /**
     * Renders content from a file. <br>
     *
     * <b>Scope:</b> Server file requst only. <br>
     * <b>Execution:</b> <code>exchangeRenderers</code>.
     *
     * @param server webdir server
     * @param exchange http exchange
     * @param source file being referenced
     * @param defaultFrontMatter default front matter configuration
     * @param bytes the file's bytes
     * @return rendered content
     *
     * @see SimpleHttpServer
     * @see SimpleHttpExchange
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public String render(final SimpleHttpServer server, final SimpleHttpExchange exchange, final File source, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        return new String(bytes);
    }

    //

    @Override
    public String toString(){
        return "Renderer{" +
               "permission='" + permission + '\'' +
               '}';
    }

}
