package com.kttdevelopment.webdir.server.pluginservice;

public final class FormatterPair {

    private final String pluginName, formatterName;

    public FormatterPair(final String pluginName, final String formatterName){
        this.pluginName = pluginName;
        this.formatterName = formatterName;
    }

    public final String getPluginName(){
        return pluginName;
    }

    public final String getFormatterName(){
        return formatterName;
    }

}
