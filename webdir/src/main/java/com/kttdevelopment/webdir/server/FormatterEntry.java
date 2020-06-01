package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.api.formatter.Formatter;

class FormatterEntry {

    private final String pluginName;

    private final String formatterName;

    private final int index;

    FormatterEntry(final int index, final String pluginName, final String formatterName){
        this.pluginName = pluginName;
        this.formatterName = formatterName;
        this.index = index;
    }

    final String getPluginName(){
        return pluginName;
    }

    final String getFormatterName(){
        return formatterName;
    }

    final int getIndex(){
        return index;
    }

    //

    private Formatter formatter = null;

    final void setAssociatedFormatter(final Formatter formatter){
        this.formatter = formatter;
    }

    final boolean hasAssociatedFormatter(){
        return formatter != null;
    }

    final Formatter getAssociatedFormatter(final Formatter formatter){
        return formatter;
    }

}
