package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.FormatterEntry;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

    private final List<FormatterEntry> formatters = new LinkedList<>();

    public final List<FormatterEntry> getFormatters(){
        return Collections.unmodifiableList(formatters);
    }

    public final void addFormatter(final String name, final Formatter formatter){
        formatters.add(new FormatterEntry() {

            private final String fn;
            private final Formatter f;
            private final String p;

            {
                this.fn = name;
                this.f = formatter;
                p = null;
            }

            @Override
            public final String getFormatterName(){
                return fn;
            }

            @Override
            public final Formatter getFormatter(){
                return f;
            }

            @Override
            public final String getPermission(){
                return p;
            }
        });
    }

    public final void addFormatter(final String name, final Formatter formatter, final String permission){
        formatters.add(new FormatterEntry() {

            private final String fn;
            private final Formatter f;
            private final String p;

            {
                this.fn = name;
                this.f = formatter;
                p = permission;
            }

            @Override
            public final String getFormatterName(){
                return fn;
            }

            @Override
            public final Formatter getFormatter(){
                return f;
            }

            @Override
            public final String getPermission(){
                return p;
            }
        });
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
