package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

    private final Map<String,Formatter> formatters = new HashMap<>();
    private final Map<Formatter,String> permissions = new HashMap<>();

    public final Map<String,Formatter> getFormatters(){
        return Collections.unmodifiableMap(formatters);
    }

    public final Map<Formatter,String> getPermissions(){
        return Collections.unmodifiableMap(permissions);
    }

    public final void addFormatter(final String name, final Formatter formatter){
        formatters.put(name,formatter);
    }

    public final void addFormatter(final String name, final Formatter formatter, final String permission){
        formatters.put(name,formatter);
        permissions.put(formatter,permission);
    }

    // instance +pluginService

    private final PluginService pluginService;

    public WebDirPlugin(final PluginService pluginService){
        this.pluginService = pluginService;
    }

    public final PluginService getPluginService(){
        return pluginService;
    }

    // override methods

    @SuppressWarnings("EmptyMethod")
    public void onEnable(){}

    @SuppressWarnings("EmptyMethod")
    public void onDisable(){}

}
