package com.kttdevelopment.webdir.pluginservice;

import com.kttdevelopment.webdir.api.WebDirPlugin;
import com.kttdevelopment.webdir.api.handler.SimpleFileHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "SpellCheckingInspection", "unchecked"})
public final class PluginLibrary {

    private final List<PluginFormatterEntry> formatters = new ArrayList<>();
    private final List<PluginFormatterEntry> preFormatters = new ArrayList<>();
    private final List<PluginFormatterEntry> postFormatters = new ArrayList<>();

    // <Handler,Permission>
    private final Map<SimpleFileHandler,String> handlerwp = new ConcurrentHashMap<>();
    private final List<SimpleFileHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    //

    public synchronized final void addPlugin(final WebDirPlugin plugin){
        final String pluginName = plugin.getPluginService().getPluginName();
        plugin.getFormatters().forEach((name, formatter) -> {
            final PluginFormatterEntry entry = new PluginFormatterEntry(pluginName, name, formatter.getValue(), formatter.getKey());
            formatters.add(entry);
            if(entry.isPreFormatter())
                preFormatters.add(entry);
            if(entry.isPostFormatter())
                postFormatters.add(entry);
        });
        plugin.getHandlers().forEach((handler, permission) -> {
            handlerwp.putIfAbsent(handler,permission);
            if(!handlers.contains(handler))
                handlers.add(handler);
        });
    }

    //

    public final PluginFormatterEntry getFormatter(final String formatter){
        for(final PluginFormatterEntry entry : formatters)
            if(entry.getFormatterName().equals(formatter))
                return entry;
        return null;
    }

    public final PluginFormatterEntry getFormatter(final String formatter, final String pluginName){
        for(final PluginFormatterEntry entry : formatters)
            if(entry.getFormatterName().equals(formatter) && entry.getPluginName().equals(pluginName))
                return entry;
        return null;
    }

    public final PluginFormatterEntry getPreFormatter(final String formatter){
        for(final PluginFormatterEntry entry : preFormatters)
            if(entry.getFormatterName().equals(formatter))
                return entry;
        return null;
    }

    public final PluginFormatterEntry getPreFormatter(final String formatter, final String pluginName){
        for(final PluginFormatterEntry entry : preFormatters)
            if(entry.getFormatterName().equals(formatter) && entry.getPluginName().equals(pluginName))
                return entry;
        return null;
    }

    public final PluginFormatterEntry getPostFormatter(final String formatter){
        for(final PluginFormatterEntry entry : postFormatters)
            if(entry.getFormatterName().equals(formatter))
                return entry;
        return null;
    }

    public final PluginFormatterEntry getPostFormatter(final String formatter, final String pluginName){
        for(final PluginFormatterEntry entry : postFormatters)
            if(entry.getFormatterName().equals(formatter) && entry.getPluginName().equals(pluginName))
                return entry;
        return null;
    }

    //

    public final List<SimpleFileHandler> getHandlers(){
        return Collections.unmodifiableList(handlers);
    }

    public final String getHandlerPermission(final SimpleFileHandler handler){
        return handlerwp.get(handler);
    }

}
