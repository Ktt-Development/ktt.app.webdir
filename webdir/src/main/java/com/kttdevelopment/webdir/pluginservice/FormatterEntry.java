package com.kttdevelopment.webdir.pluginservice;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.formatter.Formatter;

public class FormatterEntry {

    private final WebDirPlugin plugin;

    private final Formatter formatter;
    private final String formatterName;

    private final String permission;

    public FormatterEntry(final WebDirPlugin plugin, final Formatter formatter, final String formatterName, final String permission){
        this.plugin = plugin;
        this.formatter = formatter;
        this.formatterName = formatterName;
        this.permission = permission;
    }

    public final WebDirPlugin getPlugin(){
        return plugin;
    }

    public final Formatter getFormatter(){
        return formatter;
    }

    public final String getFormatterName(){
        return formatterName;
    }

    public final String getPermission(){
        return permission;
    }

}
