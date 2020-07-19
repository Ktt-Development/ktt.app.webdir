package com.kttdevelopment.webdir.api.serviceprovider;

/**
 * A configuration represented as a file.
 *
 * @see ConfigurationSection
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface ConfigurationFile extends ConfigurationSection {

    /**
     * Sets the default configuration. The configuration will use these values if none is provided from the source configuration.
     *
     * @param def default configuration
     *
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    void setDefault(final ConfigurationSection def);

    //

    /**
     * Loads or reloads the configuration from a file.
     *
     * @return if reload was successful
     * @throws UnsupportedOperationException if configuration was not loaded from a file
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean reload();

    /**
     * Saves the configuration to the file.
     *
     * @return if save was successful
     * @throws UnsupportedOperationException if configuration was not loaded from a file
     *
     * @since 01.00.00
     * @author Ktt Development
     */
    boolean save();

}
