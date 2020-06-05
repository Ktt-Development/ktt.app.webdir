package com.kttdevelopment.webdir.api;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.formatter.FormatterEntry;
import com.kttdevelopment.webdir.api.handler.HandlerEntry;
import com.kttdevelopment.webdir.api.handler.SimpleFileHandler;

import java.io.File;
import java.util.*;
import java.util.function.BiPredicate;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

    // formatters

    private final List<FormatterEntry> formatters = new LinkedList<>();

    public final List<FormatterEntry> getFormatters(){
        return Collections.unmodifiableList(formatters);
    }

    public final void addFormatter(final String name, final Formatter formatter){
        addFormatter(name,formatter,null);
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

    // handlers

    private final List<HandlerEntry> handlers = new LinkedList<>();

    public final List<HandlerEntry> getHandlers(){
        return Collections.unmodifiableList(handlers);
    }

    public final void addFileHandler(final SimpleFileHandler handler, final BiPredicate<SimpleHttpExchange,File> condition){
        addFileHandler(handler, condition, null);
    }

    public final void addFileHandler(final SimpleFileHandler handler, final BiPredicate<SimpleHttpExchange,File> condition, final String permission){
        handlers.add(new HandlerEntry() {

            private final SimpleFileHandler h;
            private final BiPredicate<SimpleHttpExchange,File> bp;
            private final String p;

            {
                h = handler;
                bp = condition;
                p = permission;
            }

            @Override
            public final SimpleFileHandler getHandler(){
                return h;
            }

            @Override
            public final BiPredicate<SimpleHttpExchange, File> getCondition(){
                return bp;
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
