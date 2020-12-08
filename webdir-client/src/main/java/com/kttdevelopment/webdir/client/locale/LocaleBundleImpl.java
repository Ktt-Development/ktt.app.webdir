package com.kttdevelopment.webdir.client.locale;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.LocaleBundle;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class LocaleBundleImpl implements LocaleBundle {

    private final ClassLoader classLoader;
    private final String resource;

    private final Map<String,String> localized = new ConcurrentHashMap<>();
    private Locale locale;

    public LocaleBundleImpl(final String resource){
        this(null, resource, LocaleBundleImpl.class.getClassLoader());
    }

    public LocaleBundleImpl(final LocaleService localeService, final String resource){
        this(localeService, resource, LocaleBundleImpl.class.getClassLoader());
    }

    public LocaleBundleImpl(final LocaleService localeService, final String resource, final ClassLoader classLoader){
        Main.getLogger().addQueuedLoggerMessage(
            "locale.name", "locale.bundle.start",
            "Locale Service", "Creating locale bundle from %s.",
            Level.FINER, resource
        );

        this.resource = resource;
        this.classLoader = classLoader;
        this.locale = Locale.getDefault();
        if(localeService != null)
            localeService.addWatchedLocale(this);
        else
            setLocale(this.locale);
    }

    public synchronized final void setLocale(final Locale locale){
        this.locale = locale;

        localized.clear();

        // populate in reverse order so specific locales override
        final String[] resources = {
            resource,
            resource + '_' + "en",
            resource + '_' + "en_US",
            resource + '_' + locale.getLanguage().substring(0, 2),
            resource + "_" + locale.getLanguage().substring(0, 2) + '_' + locale.getCountry().toUpperCase()
        };


        for(final String resource : resources){
            try(final InputStreamReader IN = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(resource + ".yml")))){
                localized.putAll(flattenYaml(MapUtility.asStringObjectMap((Map<?,?>) new YamlReader(IN).read())));
            }catch(final NullPointerException ignored){ // ignore missing
            }catch(final ClassCastException | IOException e){
                Main.getLogger().addQueuedLoggerMessage(
                    "locale.name", "locale.bundle.malformed",
                    "Locale Service", "Failed to parse locale file %s (malformed yaml). %s",
                    Level.WARNING, resource + ".yml", LoggerService.getStackTraceAsString(e)
                );
            }
        }

        if(localized.isEmpty())
            Main.getLogger().addQueuedLoggerMessage(
                "locale.name", "locale.bundle.missing",
                "Locale Service", "No locale file found or locale file was blank for resource %s.",
                Level.WARNING, resource
            );
    }

    public final Locale getLocale(){
        return locale;
    }

    private Map<String,String> flattenYaml(final Map<String,Object> map){
        return flattenYaml(map, "");
    }

    private Map<String,String> flattenYaml(final Map<String,Object> map, final String head){
        if(map == null)
            return new HashMap<>();
        final Map<String,String> OUT = new HashMap<>();

        for(final Map.Entry<String,Object> entry : map.entrySet()){
            final String key    = entry.getKey();
            final Object value  = entry.getValue();
            final String next   = head + (!head.isEmpty() ? '.' : "") + key;
            if(value instanceof Map<?,?>) // if map then flatten
                OUT.putAll(flattenYaml(MapUtility.asStringObjectMap((Map<?,?>) value), next));
            else if(value != null) // if key then add to map
                OUT.put(next, value.toString());
        }

        return OUT;
    }

    //

    @Override
    public final String getString(final String key){
        return ExceptionUtility.requireNonExceptionElse(() -> localized.get(key), null);
    }

    @Override
    public final String getString(final String key, final Object... args){
        final String value = getString(key);
        return ExceptionUtility.requireNonExceptionElse(() -> String.format(Objects.requireNonNull(value),args), value);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("resource", resource)
            .addObject("locale", locale)
            .toString();
    }

}
