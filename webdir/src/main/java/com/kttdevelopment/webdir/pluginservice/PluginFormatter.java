package com.kttdevelopment.webdir.pluginservice;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.formatter.FormatterEntry;

public class PluginFormatter {

    private final WebDirPlugin plugin;
    private final String pluginName;

    private final FormatterEntry entry;

    public PluginFormatter(final WebDirPlugin plugin, final FormatterEntry entry){
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

    public final FormatterEntry getEntry(){
        return entry;
    }

}
