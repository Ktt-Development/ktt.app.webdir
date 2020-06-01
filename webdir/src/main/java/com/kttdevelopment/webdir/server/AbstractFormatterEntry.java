package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.api.formatter.Formatter;

import java.util.Objects;

class AbstractFormatterEntry {

    private final String pluginName;

    private final String formatterName;

    AbstractFormatterEntry(final String pluginName, final String formatterName){
        this.pluginName = pluginName;
        this.formatterName = formatterName;
    }

    final String getPluginName(){
        return pluginName;
    }

    final String getFormatterName(){
        return formatterName;
    }

    @Override
    public final boolean equals(final Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final AbstractFormatterEntry that = (AbstractFormatterEntry) o;
        return Objects.equals(pluginName, that.pluginName) &&
               Objects.equals(formatterName, that.formatterName);
    }

    @Override
    public final int hashCode(){
        return Objects.hash(pluginName, formatterName);
    }

}
