package com.kttdevelopment.webdir.api.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * A renderer that determines how content will appear based on an exchange. Only clients with the correct permission will use this.
 * <br>
 * {@link #render(File, ConfigurationSection, String)} is not affected by the permissions requirement and will run in all cases.
 *
 * @see com.kttdevelopment.webdir.api.Renderer
 * @see ExchangeRenderAdapter
 * @see SimpleHttpExchange
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public class ExchangeRenderer implements ExchangeRenderAdapter {

    private final String permission;

    /**
     * Creates an exchange renderer with no required permission.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public ExchangeRenderer(){
        permission = null;
    }

    /**
     * Creates an exchange renderer with a required permission.
     *
     * @param permission required permission
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public ExchangeRenderer(final String permission){
        this.permission = permission;
    }

    //

    @Override
    public String render(final File output, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    @Override
    public String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    //

    /**
     * Returns the required permission to use the exchange renderer or null if none is required.
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

    @Override
    public String toString(){
        return "ExchangeRenderer{" +
               "permission='" + permission + '\'' +
               '}';
    }

}
