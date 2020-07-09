package com.kttdevelopment.webdir.api.serviceprovider;

import java.util.List;
import java.util.Map;

/**
 * A simplified implementation of map reading.
 *
 * @see ConfigurationFile
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
@SuppressWarnings("rawtypes")
public interface ConfigurationSection {

    /**
     * Returns the top most map object.
     *
     * @return top most map
     *
     * @see #getParent()
     * @since 01.00.00
     * @author Ktt Development
     */
    ConfigurationSection getRoot();

    /**
     * Returns the parent of the map object or null if it is already the top most.
     *
     * @return map above the current level
     *
     * @see #getRoot()
     * @since 01.00.00
     * @author Ktt Development
     */
    ConfigurationSection getParent();

    /**
     * Returns a configuration section for the key.
     *
     * @param key key
     * @return configuration section
     * @throws ClassCastException if value for key is not a map
     *
     * @see #getMap(String)
     * @see #getMap(String, Class, Class)
     * @see #getMap(String, Map)
     * @since 01.00.00
     * @author Ktt Development
     */
    ConfigurationSection get(final String key);

    //

    /**
     * Returns the default configuration for the key.
     *
     * @return default configuration
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    ConfigurationSection getDefault();

    /**
     * Sets the default configuration value for a key.
     *
     * @param key key
     * @param value value
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    void setDefault(final String key, final Object value);

    //

    /**
     * Returns if map contains a value or default value for the key.
     *
     * @param key key to check
     * @return if value for the key exists
     *
     * @see #contains(String, boolean)
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean contains(final String key);

    /**
     * Returns if map contains a value for the key.
     *
     * @param key key to check
     * @param ignoreDefault whether to included default configuration or not
     * @return if value for the key exists
     *
     * @see #contains(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean contains(final String key, final boolean ignoreDefault);

    //

    /**
     * Returns the boolean value for the key or false if the value is null or not a boolean.
     *
     * @param key key
     * @return boolean
     *
     * @see #getBoolean(String, boolean)
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean getBoolean(final String key);

    /**
     * Returns the boolean value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return boolean
     *
     * @see #getBoolean(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean getBoolean(final String key, final boolean def);

    /**
     * Returns the character value for the key.
     *
     * @param key key
     * @return character
     * @throws IndexOutOfBoundsException if key value has no character
     * @throws NullPointerException if no value is found for the key
     *
     * @see #getCharacter(String, char)
     * @since 01.00.00
     * @author Ktt Development
     */
    char getCharacter(final String key);

    /**
     * Returns the character value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return character
     *
     * @see #getCharacter(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    char getCharacter(final String key, final char def);

    /**
     * Returns the string value for the key.
     *
     * @param key key
     * @return string
     *
     * @see #getString(String, String)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final String key);

    /**
     * Returns the string value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return string
     *
     * @see #getString(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    String getString(final String key, final String def);

    /**
     * Returns the float value for the key.
     *
     * @param key key
     * @return float
     * @throws NumberFormatException if key value is not a float
     * @throws NullPointerException if no value is found for the key
     *
     * @see #getFloat(String, float)
     * @since 01.00.00
     * @author Ktt Development
     */
    float getFloat(final String key);

    /**
     * Returns the float value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return float
     *
     * @see #getFloat(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    float getFloat(final String key, final float def);

    /**
     * Returns the integer value for the key.
     *
     * @param key key
     * @return integer
     * @throws NumberFormatException if key value is not an integer
     * @throws NullPointerException if no value is found for the key
     *
     * @see #getInteger(String, int)
     * @since 01.00.00
     * @author Ktt Development
     */
    int getInteger(final String key);

    /**
     * Returns the integer value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return integer
     *
     * @see #getInteger(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    int getInteger(final String key, final int def);

    /**
     * Returns the double value for the key.
     *
     * @param key key
     * @return double
     * @throws NumberFormatException if key value is not an integer
     * @throws NullPointerException if no value is found for the key
     *
     * @see #getDouble(String, double)
     * @since 01.00.00
     * @author Ktt Development
     */
    double getDouble(final String key);

    /**
     * Returns the double value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return double
     *
     * @see #getDouble(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    double getDouble(final String key, final double def);

    /**
     * Returns the long value for the key.
     *
     * @param key key
     * @return long
     * @throws NumberFormatException if key value is not a long
     * @throws NullPointerException if no value is found for the key
     *
     * @see #getLong(String, long)
     * @since 01.00.00
     * @author Ktt Development
     */
    long getLong(final String key);

    /**
     * Returns the long value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @return long
     *
     * @see #getLong(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    long getLong(final String key, final long def);

    //

    /**
     * Returns the value for the key.
     *
     * @param key key
     * @return object
     *
     * @see #getObject(String, Object)
     * @see #getObject(String, Class)
     * @since 01.00.00
     * @author Ktt Development
     */
    Object getObject(final String key);

    /**
     * Returns a casted value for the key.
     *
     * @param key key
     * @param type value class
     * @param <T> value type
     * @return value
     * @throws ClassCastException if value could not be cast to type
     *
     * @see #getObject(String)
     * @see #getObject(String, Object)
     * @since 01.00.00
     * @author Ktt Development
     */
    <T> T getObject(final String key, final Class<T> type);

    /**
     * Returns the value for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @param <T> value type
     * @return value
     *
     * @see #getObject(String)
     * @see #getObject(String, Class)
     * @since 01.00.00
     * @author Ktt Development
     */
    <T> T getObject(final String key, T def);

    /**
     * Returns the map for the key.
     *
     * @param key key
     * @return map
     * @throws ClassCastException if value for key is not a map
     *
     * @see #get(String)
     * @see #getMap(String, Class, Class)
     * @see #getMap(String, Map)
     * @since 01.00.00
     * @author Ktt Development
     */
    Map getMap(final String key);

    /**
     * Returns a casted map for the key.
     *
     * @param key key
     * @param keyType key class
     * @param valueType value class
     * @param <K> key type
     * @param <V> value type
     * @return map
     * @throws ClassCastException if value for key is not a map or if key/value types were not compatible
     *
     * @see #get(String)
     * @see #getMap(String)
     * @see #getMap(String, Map)
     */
    <K,V> Map<K,V> getMap(final String key, final Class<K> keyType, final Class<V> valueType);

    /**
     * Returns the map for the key,
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @param <K> key type
     * @param <V> value type
     * @return map
     *
     * @see #get(String)
     * @see #getMap(String, Class, Class)
     * @see #getMap(String)
     * @since 01.00.00
     * @author Ktt Development
     */
    <K,V> Map<K,V> getMap(final String key, final Map<K,V> def);

    /**
     * Returns the list for the key.
     *
     * @param key key
     * @return value
     * @throws ClassCastException if value for key is not a list or string
     *
     * @see #getList(String, Class)
     * @see #getList(String, List)
     * @since 01.00.00
     * @author Ktt Development
     */
    List getList(final String key);

    /**
     * Returns a casted list for the key.
     *
     * @param key key
     * @param type list class
     * @param <T> list type
     * @return list
     * @throws ClassCastException if value for key is not a list or string; or if list type is not compatible
     *
     * @see #getList(String)
     * @see #getList(String, List)
     * @since 01.00.00
     * @author Ktt Development
     */
    <T> List<T> getList(final String key, final Class<T> type);

    /**
     * Returns the list for the key.
     *
     * @param key key
     * @param def default value (instead of using the default configuration value)
     * @param <T> list type
     * @return list
     *
     * @see #getList(String)
     * @see #getList(String, Class)
     * @since 01.00.00
     * @author Ktt Development
     */
    <T> List<T> getList(final String key, final List<T> def);

    //

    /**
     * Sets the value for the key.
     *
     * @param key key
     * @param value value
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    void set(final String key, final Object value);

    //

    /**
     * Returns configuration as a map.
     *
     * @return map
     *
     * @see #toMapWithDefaults()
     * @since 01.00.00
     * @author Ktt Development
     */
    Map toMap();

    /**
     * Returns configuration as a map with default values.
     *
     * @return map
     *
     * @see #toMap() ()
     * @since 01.00.00
     * @author Ktt Development
     */
    Map toMapWithDefaults();

}
