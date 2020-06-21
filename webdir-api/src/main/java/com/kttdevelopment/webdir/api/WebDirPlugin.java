package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.handler.SimpleFileHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

    // formatters

    private final Map<String, Map.Entry<Formatter,String>> formatters = new ConcurrentHashMap<>();

    public final Map<String, Map.Entry<Formatter,String>> getFormatters(){
        return Collections.unmodifiableMap(formatters);
    }

    public synchronized final void addFormatter(final String name, final Formatter formatter){
        addFormatter(name,formatter,"");
    }

    public synchronized final void addFormatter(final String name, final Formatter formatter, final String permission){
        formatters.put(name, new Map.Entry<>() {

            @Override
            public final Formatter getKey(){
                return formatter;
            }

            @Override
            public final String getValue(){
                return permission;
            }

            @Override
            public String setValue(final String value){
                throw new UnsupportedOperationException();
            }
        });
    }

    // handlers

    private final Map<SimpleFileHandler,String> handlers = new ConcurrentHashMap<>();

    public final Map<SimpleFileHandler,String> getHandlers(){
        return Collections.unmodifiableMap(handlers);
    }

    public synchronized final void addHandler(final SimpleFileHandler handler){
        addHandler(handler,"");
    }

    public synchronized final void addHandler(final SimpleFileHandler handler, final String permission){
        handlers.put(handler,permission);
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
