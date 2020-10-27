package com.kttdevelopment.webdir.client.locale;

import com.amihaiemil.eoyaml.*;
import com.kttdevelopment.webdir.api.LocaleBundle;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class LocaleBundleImpl implements LocaleBundle {

    private final String resource;

    private final Map<String,String> localized = new ConcurrentHashMap<>();
    private Locale locale;

    public LocaleBundleImpl(final String resource){
        this(null, resource);
    }

    public LocaleBundleImpl(final LocaleService localeService, final String resource){
        Main.getLogger().addQueuedLoggerMessage(
            "locale.name", "locale.bundle.start",
            "Locale Service", "Creating bundle for service %s at %s.",
            Level.FINER, localeService, resource
        );

        this.resource = resource;
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
            try{
                localized.putAll(flattenYaml(Yaml.createYamlInput(getClass().getClassLoader().getResourceAsStream(resource + ".yml")).readYamlMapping()));
            }catch(final NullPointerException e){ // ignore missing
            }catch(final IOException e){
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

    private Map<String,String> flattenYaml(final YamlMapping map){
        return flattenYaml(map, "");
    }

    private Map<String,String> flattenYaml(final YamlMapping map, final String head){
        if(map == null)
            return new HashMap<>();
        final Map<String,String> OUT = new HashMap<>();

        for(final YamlNode node : map.keys()){
            final String key = asString(node);
            final YamlMapping mapping = map.yamlMapping(node);
            final String next = head + (!head.isEmpty() ? '.' : "") + key;
            if(mapping != null)
                OUT.putAll(flattenYaml(map.yamlMapping(node), next)); // if map dive further
            else
                OUT.put(next, map.string(node)); // if key then add to map
        }
        return OUT;
    }

    private String asString(final YamlNode e){
        return ExceptionUtility.requireNonExceptionElse(() -> e.asScalar().value(), null);
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
            .addObject("localized", localized)
            .addObject("locale", locale)
            .toString();
    }

}
