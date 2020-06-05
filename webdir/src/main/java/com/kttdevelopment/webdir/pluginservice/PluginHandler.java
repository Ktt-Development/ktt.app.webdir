package com.kttdevelopment.webdir.pluginservice;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.formatter.FormatterEntry;
import com.kttdevelopment.webdir.api.handler.HandlerEntry;

public class PluginHandler {

    private final WebDirPlugin plugin;
    private final String pluginName;

    private final HandlerEntry entry;

    public PluginHandler(final WebDirPlugin plugin, final HandlerEntry entry){
        this.plugin = plugin;
        this.pluginName = plugin.getPluginService().getPluginName();
        this.entry = entry;
    }

    public final WebDirPlugin getPlugin(){
        return plugin;
    }

    public final String getPluginName(){
        return pluginName;
    }

    public final HandlerEntry getEntry(){
        return entry;
    }

}
