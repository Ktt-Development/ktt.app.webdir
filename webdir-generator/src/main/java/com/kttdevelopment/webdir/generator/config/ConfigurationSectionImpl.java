package com.kttdevelopment.webdir.generator.config;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationSectionImpl implements ConfigurationSection {

    private final ConfigurationSection root;
    private final ConfigurationSection parent;

    private final Map def;
    protected Map config;

    public ConfigurationSectionImpl(){
        this(new HashMap(),null);
    }

    public ConfigurationSectionImpl(final Map config){
        this(null, null, config, (Map) null);
    }

    public ConfigurationSectionImpl(final Map config, final ConfigurationSection def){
        this(null,null,config,def);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final Map config, final ConfigurationSection def){
        this(root,null,config,def);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final Map config){
        this(root, null, config, (Map) null);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final ConfigurationSection parent, final Map config){
        this(root, parent, config, (Map) null);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final ConfigurationSection parent, final Map config, final ConfigurationSection def){
        this(root,parent,config,def == null ? null : def.toMapWithDefaults());
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final ConfigurationSection parent, final Map config, final Map def){
        this.root   = root == null  ? this          : root;
        this.parent = parent;
        this.config = config;
        this.def    = def == null   ? new HashMap() : def;
    }

    //

    @Override
    public final ConfigurationSection getRoot(){
        return root;
    }

    @Override
    public final ConfigurationSection getParent(){
        return parent;
    }

    @Override
    public final ConfigurationSection get(final String key){
        return new ConfigurationSectionImpl(root,this,getMap(key));
    }

    //

    @Override
    public final ConfigurationSection getDefault(){
        return new ConfigurationSectionImpl(def);
    }

    @Override
    public final void setDefault(final String key, final Object value){
        def.put(key,value);
    }

    @Override
    public final void setDefault(final ConfigurationSection def){
        this.def.putAll(def.toMap());
    }

    @Override
    public final void clearDefault(){
        def.clear();
    }

    //

    @Override
    public final boolean contains(final String key){
        return config.containsKey(key) || def.containsKey(key);
    }

    @Override
    public final boolean contains(final String key, final boolean ignoreDefault){
        return config.containsKey(key);
    }

    //

    @Override
    public final boolean getBoolean(final String key){
        return Boolean.parseBoolean(config.getOrDefault(key,def.get(key)).toString());
    }

    @Override
    public final boolean getBoolean(final String key, final boolean def){
        try{
            return Boolean.parseBoolean(Objects.requireNonNull(config.get(key)).toString());
        }catch(final NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final char getCharacter(final String key){
        return config.getOrDefault(key,def.get(key)).toString().charAt(0);
    }

    @Override
    public final char getCharacter(final String key, final char def){
        try{
            return Objects.requireNonNull(config.get(key)).toString().charAt(0);
        }catch(final IndexOutOfBoundsException | NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final String getString(final String key){
        final Object value = config.getOrDefault(key,def.get(key));
        return value != null ? (value instanceof List ? ((List) value).get(0) : value).toString() : null;
    }

    @Override
    public final String getString(final String key, final String def){
        final Object value = config.getOrDefault(key,def);
        return (value instanceof List ? ((List) value).get(0) : value).toString();
    }

    @Override
    public final float getFloat(final String key){
        return Float.parseFloat(config.getOrDefault(key,def.get(key)).toString());
    }

    @Override
    public final float getFloat(final String key, final float def){
        try{
            return Float.parseFloat(Objects.requireNonNull(config.get(key)).toString());
        }catch(final NumberFormatException | NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final int getInteger(final String key){
        return (int) Double.parseDouble(config.getOrDefault(key,def.get(key)).toString());
    }

    @Override
    public final int getInteger(final String key, final int def){
        try{
            return Integer.parseInt(Objects.requireNonNull(config.get(key)).toString());
        }catch(final NumberFormatException | NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final double getDouble(final String key){
        return Double.parseDouble(config.getOrDefault(key,def.get(key)).toString());
    }

    @Override
    public final double getDouble(final String key, final double def){
        try{
            return Double.parseDouble(Objects.requireNonNull(config.get(key)).toString());
        }catch(final NumberFormatException | NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final long getLong(final String key){
        return Long.parseLong(config.getOrDefault(key,def.get(key)).toString());
    }

    @Override
    public final long getLong(final String key, final long def){
        try{
            return Long.parseLong(Objects.requireNonNull(config.get(key)).toString());
        }catch(final NumberFormatException | NullPointerException ignored){
            return def;
        }
    }

    //

    @Override
    public final Object getObject(final String key){
        return config.getOrDefault(key,def.get(key));
    }

    @Override
    public final <T> T getObject(final String key, final Class<T> type){
        return (T) config.getOrDefault(key,def.get(key));
    }

    @Override
    public final <T> T getObject(final String key, final T def){
        try{
            return Objects.requireNonNullElse((T) config.get(key),def);
        }catch(final ClassCastException ignored){
            return def;
        }
    }

    @Override
    public final Map getMap(final String key){
        return (Map) config.getOrDefault(key,def.get(key));
    }

    @Override
    public final <K,V> Map<K,V> getMap(final String key, final Class<K> keyType, final Class<V> valueType){
        return (Map<K,V>) getMap(key);
    }

    @Override
    public final <K,V> Map<K,V> getMap(final String key, final Map<K,V> def){
        try{
            return (Map<K, V>) Objects.requireNonNullElse(config.get(key), def);
        }catch(final ClassCastException ignored){
            return def;
        }
    }

    @Override
    public final List getList(final String key){
        final Object v = config.getOrDefault(key,def.get(key));
        return v instanceof String ? Collections.singletonList(v) : (List) v;
    }

    @Override
    public final <T> List<T> getList(final String key, final Class<T> type){
        return (List<T>) getList(key);
    }

    @Override
    public final <T> List<T> getList(final String key, final List<T> def){
        final Object v = config.getOrDefault(key,def);
        try{
            return v instanceof String ? (List<T>) Collections.singletonList(v) : (List<T>) v;
        }catch(final ClassCastException ignored){
            return def;
        }
    }

    //

    @Override
    public final void set(final String key, final Object value){
        config.put(key,value);
    }

    //

    @Override
    public final Map toMap(){
        return new HashMap(config);
    }

    @Override
    public Map toMapWithDefaults(){
        final Map map = new HashMap(def);
        map.putAll(config);
        return map;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof ConfigurationSection))
            return false;
        final ConfigurationSection other = (ConfigurationSection) o;
        return other.toMapWithDefaults().equals(toMapWithDefaults());
    }

    @Override
    public String toString(){
        return new toStringBuilder("ConfigurationSection")
            .addObject("root",root == this ? "this" : root)
            .addObject("parent",parent)
            .addObject("config",config)
            .addObject("def",def)
            .toString();
    }

}
