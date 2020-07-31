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
 * @see FileRenderAdapter
 * @see ExchangeRenderer
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public class FileRenderer extends ExchangeRenderer implements FileRenderAdapter{

    /**
     * Creates a file renderer with no required permission.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public FileRenderer(){
        super();
    }

    /**
     * Creates a file renderer with a required permission.
     *
     * @param permission required permission
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public FileRenderer(final String permission){
        super(permission);
    }

    //

    @Override
    public String render(final SimpleHttpExchange exchange, final File source, final byte[] bytes){
        return new String(bytes);
    }

    //

    @Override
    public String toString(){
        return "FileRenderer{" +
               "permission='" + getPermission() + '\'' +
               '}';
    }

}
