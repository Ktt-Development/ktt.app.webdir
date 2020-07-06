package com.kttdevelopment.webdir.pluginservice;

public final class PluginFormatterEntry {

    private final String pluginName, formatterName, permission;
    private final Formatter formatter;

    public PluginFormatterEntry(final String pluginName, final String formatterName, final Formatter formatter){
        this.pluginName = pluginName;
        this.formatterName = formatterName;
        this.permission = "";
        this.formatter = formatter;
    }

    public PluginFormatterEntry(final String pluginName, final String formatterName, final String permission, final Formatter formatter){
        this.pluginName = pluginName;
        this.formatterName = formatterName;
        this.permission = permission;
        this.formatter = formatter;
    }

    public final String getPluginName(){
        return pluginName;
    }

    public final String getFormatterName(){
        return formatterName;
    }

    public final String getPermission(){
        return permission.isBlank() ? null : permission;
    }

    public final Formatter getFormatter(){
        return formatter;
    }

    public final boolean isPreFormatter(){
        return formatter instanceof PreFormatter;
    }

    public final boolean isPostFormatter(){
        return formatter instanceof PostFormatter;
    }

}
