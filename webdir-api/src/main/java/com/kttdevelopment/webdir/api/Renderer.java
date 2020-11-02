package com.kttdevelopment.webdir.api;

import java.nio.charset.StandardCharsets;

/**
 * A renderer determines how context will appear.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Ktt Development
 */
public class Renderer {

    private final String permission;

    /**
     * Creates a renderer with no required permission.
     *
     * @since 1.0.0
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
     * @since 1.0.0
     * @author Ktt Development
     */
    public Renderer(final String permission){
        this.permission = permission;
    }

    /**
     * Returns the permission required to use the renderer or null if no permission is required.
     *
     * @return required permission
     *
     * @since 1.0.0
     * @author Ktt Development
     */
    public final String getPermission(){
        return permission;
    }

    //

    /**
     * Renders content from a file.
     *
     * @param render file information, see {@link FileRender}
     * @return rendered content
     *
     * @see FileRender
     * @since 1.0.0
     * @author Ktt Development
     */
    public byte[] render(final FileRender render){
        return render.getContentAsBytes();
    }

    /**
     * Converts a string to a byte array with UTF-8 encoding.
     *
     * @param string string to encode
     * @return string as bytes
     *
     * @since 1.0.0
     * @author KTt Development
     */
    public final byte[] asBytes(final String string){
        return string.getBytes(StandardCharsets.UTF_8);
    }

    //

    @Override
    public String toString(){
        return "Renderer{" +
               "permission='" + permission + '\'' +
               '}';
    }

}
