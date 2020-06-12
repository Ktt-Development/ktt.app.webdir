package com.kttdevelopment.webdir.api.serviceprovider;

import java.io.IOException;

/**
 * A configuration file used to store settings from a yaml file. Represents the root level {@link ConfigurationSection}.
 *
 * @see ConfigurationSection
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface ConfigurationFile extends ConfigurationSection {

    /**
     * Sets the default configuration. Used when configuration file has no no value for the key requested.
     *
     * @param def Default configuration
     *
     * @see ConfigurationFile
     * @since 01.00.00
     * @author Ktt Development
     */
    void setDefault(final ConfigurationFile def);

    //

    /**
     * Clears loaded configuration data from memory and re-reads the configuration file.
     *
     * @since 01.00.00
     */
    void reload();

    /**
     * Saves the configuration to file.
     *
     * @see #saveDefault()
     * @since 01.00.00
     */
    void save();

    /**
     * Saves the default configuration to file only if it does not exist.
     *
     * @see #save()
     * @since 01.00.00
     */
    void saveDefault();

}
