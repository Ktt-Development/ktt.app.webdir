package com.kttdevelopment.webdir.api;

import java.nio.charset.StandardCharsets;

public class Renderer {

    private final String permission;

    public Renderer(){
        this.permission = null;
    }

    public Renderer(final String permission){
        this.permission = permission;
    }

    public final String getPermission(){
        return permission;
    }

    //

    public byte[] render(final FileRender render){
        return render.getContentAsBytes();
    }

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
