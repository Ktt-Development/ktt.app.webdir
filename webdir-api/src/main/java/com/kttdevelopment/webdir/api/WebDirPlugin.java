package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

    private final Map<String,Formatter> formatters = new HashMap<>();

    public final Map<String,Formatter> getFormatters(){
        return Collections.unmodifiableMap(formatters);
    }

    public final void addFormatter(final String name, final Formatter formatter){
        formatters.put(name,formatter);
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

    public void onEnable(){}

    public void onDisable(){}

}
