package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;

import java.util.*;

public class WebDirPlugin {

    private final List<Formatter> formatters = new ArrayList<>();

    public final List<Formatter> getFormatters(){
        return Collections.unmodifiableList(formatters);
    }

    public final void addFormatter(final Formatter formatter){
        formatters.add(formatter);
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
