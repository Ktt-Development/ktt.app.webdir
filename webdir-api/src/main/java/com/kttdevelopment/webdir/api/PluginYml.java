package com.kttdevelopment.webdir.api;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

/**
 * This class represents the plugin.yml file of a plugin as an object.
 *
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public abstract class PluginYml {

    /**
     * Returns the plugin name provided by the <code>name</code> key. <i>required</i>
     *
     * @return plugin name
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String getPluginName();

    /**
     * Returns the plugin version provided by the <code>version</code> key.
     *
     * @return plugin version
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String getPluginVersion();

    /**
     * Returns the plugin author or first plugin author provided by the <code>authors</code> key.
     *
     * @return author or first author
     *
     * @see #getAuthors()
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String getAuthor();

    /**
     * Returns a list of all the plugin authors provided by the <code>authors</code> key.
     *
     * @return list of authors
     *
     * @see #getAuthor()
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String[] getAuthors();

    /**
     * Returns a list of plugin dependencies provided by the <code>dependencies</code> key.
     *
     * @return list of plugin dependencies
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract String[] getDependencies();

    /**
     * Returns the plugin.yml file as a configuration.
     *
     * @return configuration
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    public abstract ConfigurationSection getConfiguration();

}
