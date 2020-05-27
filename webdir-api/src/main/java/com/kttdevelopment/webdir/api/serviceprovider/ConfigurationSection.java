package com.kttdevelopment.webdir.api.serviceprovider;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface ConfigurationSection {

    ConfigurationSection getRoot();
    
    ConfigurationSection getParent();
    
    ConfigurationSection get(final String key);

    //

    ConfigurationSection getDefault();

    void setDefault(final String key, final Object value);

    //
    
    boolean contains(final String key);

    boolean contains(final String key, final boolean ignoreDefault);

    //

    boolean getBoolean(final String key);

    boolean getBoolean(final String key, final boolean def);

    char getCharacter(final String key);

    char getCharacter(final String key, final char def);

    String getString(final String key);

    String getString(final String key, final String def);

    float getFloat(final String key);

    float getFloat(final String key, final float def);

    int getInteger(final String key);

    int getInteger(final String key, final int def);

    double getDouble(final String key);

    double getDouble(final String key, final double def);

    long getLong(final String key);

    long getLong(final String key, final long def);

    //

    Object getObject(final String key);

    Object getObject(final String key, final Object def);

    <T> T getObject(final String key, final Class<T> type);

    <T> T getObject(final String key, T def, final Class<T> type);

    Map getMap(final String key);

    Map getMap(String key, Map def);

    <K,V> Map<K,V> getMap(final String key, final Class<K> keyType, final Class<V> valueType);

    <K,V> Map<K,V> getMap(final String key, final Map<K,V> def, final Class<K> keyType, final Class<V> valueType);

    List getList(final String key);

    List getList(final String key, final List def);

    <T> List<T> getList(final String key, final Class<T> type);

    <T> List<T> getList(final String key, final List<T> def, final Class<T> type);

    //

    void set(final String key, final Object value);

    //

    Map toMap();

}
