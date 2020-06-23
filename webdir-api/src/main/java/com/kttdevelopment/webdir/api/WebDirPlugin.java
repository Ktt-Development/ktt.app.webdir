package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.formatter.Formatter;
import com.kttdevelopment.webdir.api.handler.SimpleFileHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("SpellCheckingInspection")
public class WebDirPlugin {

// formatters

    private final Map<String, Map.Entry<Formatter,String>> formatters = new ConcurrentHashMap<>();

    /**
     * Returns an umodifiable map of the currently added formatters. <b>Reserved for WebDir</b>
     *
     * @return map of formatters.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public final Map<String, Map.Entry<Formatter,String>> getFormatters(){
        return Collections.unmodifiableMap(formatters);
    }

    /**
     * Adds a formatter to the plugin.
     *
     * @param name name of formatter
     * @param formatter formatter
     *
     * @see #addFormatter(String, Formatter, String)
     * @since 01.00.00
     * @author Ktt Development
     */
    public synchronized final void addFormatter(final String name, final Formatter formatter){
        addFormatter(name,formatter,"");
    }

    /**
     * Adds a formatter to the plugin.
     *
     * @param name name of formatter
     * @param formatter formatter
     * @param permission permission required to use formatter
     *
     * @see #addFormatter(String, Formatter)
     * @since 01.00.00
     * @author Ktt Development
     */
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

    /**
     * Returns an umodifiable map of the currently added handlers. <b>Reserved for WebDir</b>
     *
     * @return map of handlers
     *
     * @see SimpleFileHandler
     * @since 01.00.00
     * @author Ktt Development
     */
    public final Map<SimpleFileHandler,String> getHandlers(){
        return Collections.unmodifiableMap(handlers);
    }

    /**
     * Adds a file handler to the plugin.
     *
     * @param handler handler
     *
     * @see #addHandler(SimpleFileHandler, String)
     * @since 01.00.00
     * @author Ktt Development
     */
    public synchronized final void addHandler(final SimpleFileHandler handler){
        addHandler(handler,"");
    }

    /**
     * Adds a file handler to the plugin.
     *
     * @param handler handler
     * @param permission permission required to use handler
     *
     * @see #addHandler(SimpleFileHandler)
     * @since 01.00.00
     * @author Ktt Development
     */
    public synchronized final void addHandler(final SimpleFileHandler handler, final String permission){
        handlers.put(handler,permission);
    }

// instance +pluginService

    private final PluginService pluginService;

    /**
     * Instantiates a plugin. <b>Reserved for WebDir</b>
     *
     * @param pluginService plugin service implementation
     */
    public WebDirPlugin(final PluginService pluginService){
        this.pluginService = pluginService;
    }

    /**
     * Returns the WebDir API Implementation.
     *
     * @return plugin service
     *
     * @see PluginService
     * @since 01.00.00
     * @author Kt tDevelopment
     */
    public final PluginService getPluginService(){
        return pluginService;
    }

// override methods

    /**
     * Handles when the plugin is enabled.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    @SuppressWarnings("EmptyMethod")
    public void onEnable(){}

    /**
     * Handles when the plugin is disabled.
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    @SuppressWarnings("EmptyMethod")
    public void onDisable(){}

}
