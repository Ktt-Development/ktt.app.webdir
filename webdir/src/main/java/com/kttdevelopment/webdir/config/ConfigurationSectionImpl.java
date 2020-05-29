package com.kttdevelopment.webdir.config;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationSectionImpl implements ConfigurationSection {

    private final ConfigurationSection root;
    private final ConfigurationSection parent;
    protected ConfigurationSection def;

    protected Map config; // todo: convert this to ConfigurationSection & constructor params as well

    public ConfigurationSectionImpl(){
        this.root = this;
        this.parent = null;
        this.config = new HashMap();
        this.def = new ConfigurationSectionImpl(this,null,new HashMap(),null);
    }

    public ConfigurationSectionImpl(final Map config){
        this.root = this;
        this.parent = null;
        this.config = config;
        this.def = new ConfigurationSectionImpl(this,null,new HashMap(),null);
    }

    public ConfigurationSectionImpl(final Map config, final ConfigurationSection def){
        this.root = this;
        this.parent = null;
        this.config = config;
        this.def = def;
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final Map config, final ConfigurationSection def){
        this.root = root;
        this.parent = null;
        this.config = config;
        this.def = def;
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final Map config){
        this.root = root;
        this.parent = null;
        this.config = config;
        this.def = new ConfigurationSectionImpl(this,null,new HashMap(),null);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final ConfigurationSection parent, final Map config){
        this.root = root;
        this.parent = parent;
        this.config = config;
        this. def = new ConfigurationSectionImpl(this,null,new HashMap(),null);
    }

    public ConfigurationSectionImpl(final ConfigurationSection root, final ConfigurationSection parent, final Map config, final ConfigurationSection def){
        this.root = root;
        this.parent = parent;
        this.config = config;
        this.def = def;
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
        return new ConfigurationSectionImpl(root,parent,getMap(key));
    }

    //

    @Override
    public final ConfigurationSection getDefault(){
        if(def == null)
            throw new UnsupportedOperationException();
        return def;
    }

    @Override
    public final void setDefault(final String key, final Object value){
        if(def == null)
            throw new UnsupportedOperationException();
        def.set(key,value);
    }

    //

    @Override
    public final boolean contains(final String key){
        return config.containsKey(key) || def.contains(key);
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
        }catch(final NumberFormatException | NullPointerException ignored){
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
        }catch(final IndexOutOfBoundsException | NumberFormatException | NullPointerException ignored){
            return def;
        }
    }

    @Override
    public final String getString(final String key){
        return config.getOrDefault(key,def.get(key)).toString();
    }

    @Override
    public final String getString(final String key, final String def){
        return Objects.requireNonNullElse(config.get(key).toString(),def);
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
        return Integer.parseInt(config.getOrDefault(key,def.get(key)).toString());
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
    public final Object getObject(final String key, final Object def){
        return Objects.requireNonNullElse(config.get(key),def);
    }

    @Override
    public final <T> T getObject(final String key, final Class<T> type){
        return (T) config.getOrDefault(key,def.get(key));
    }

    @Override
    public final <T> T getObject(final String key, final T def, final Class<T> type){
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
    public final Map getMap(final String key, final Map def){
        try{
            return (Map) config.getOrDefault(key, def);
        }catch(final ClassCastException ignored){
            return def;
        }
    }

    @Override
    public final <K, V> Map<K, V> getMap(final String key, final Class<K> keyType, final Class<V> valueType){
        return (Map<K,V>) getMap(key);
    }

    @Override
    public final <K, V> Map<K, V> getMap(final String key, final Map<K, V> def, final Class<K> keyType, final Class<V> valueType){
        try{
            return (Map<K,V>) getMap(key,def);
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
    public final List getList(final String key, final List def){
        try{
            final Object v = config.getOrDefault(key,def);
            return v instanceof String ? Collections.singletonList(v) : (List) v;
        }catch(final ClassCastException ignored){
            return def;
        }
    }

    @Override
    public final <T> List<T> getList(final String key, final Class<T> type){
        return (List<T>) getList(key);
    }

    @Override
    public final <T> List<T> getList(final String key, final List<T> def, final Class<T> type){
        try{
            return (List<T>) getList(key,def);
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

}
