package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

public class ExchangeRenderer implements Renderer {

    private final String permission;

    public ExchangeRenderer(){
        permission = null;
    }

    public ExchangeRenderer(final String permission){
        this.permission = permission;
    }

    //

    @Override
    public String render(final File output, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    // maybe change source to path?
    public String render(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content){
        return content;
    }

    //

    public final String getPermissions(){
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
