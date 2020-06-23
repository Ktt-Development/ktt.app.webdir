package com.kttdevelopment.webdir.api.formatter;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;

/**
 * Formats server files whenever an exchange requests it.
 *
 * @see PreFormatter
 * @since 01.00.00
 * @version 01.00.00
 * @author Ktt Development
 */
public interface PostFormatter extends Formatter {

    /**
     * Formats file content.
     *
     * @param exchange the http exchange from the client that is accessing the file
     * @param source file that is being formatted
     * @param yamlFrontMatter yaml front matter for the file; null if none present
     * @param content file content with a strong
     * @return formatted file
     *
     * @see SimpleHttpExchange
     * @see ConfigurationSection
     * @since 01.00.00
     * @author Ktt Development
     */
    String format(final SimpleHttpExchange exchange, final File source, final ConfigurationSection yamlFrontMatter, final String content);

}
