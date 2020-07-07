package com.kttdevelopment.webdir.server.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;
import java.util.function.BiPredicate;

/**
 * Handles formatting for files.
 *
 * @author Ktt Development
 * @since 01.00.00
 * @version 01.00.00
 */
public interface SimpleFileHandler extends BiPredicate<SimpleHttpExchange,File> {

    /**
     * Determines on what files the handler should be used on (ex: file extension).
     *
     * @param exchange the http exchange from the client that is accessing the file
     * @param file file being referenced
     * @return whether the file should be formatted or not
     */
    @Override
    boolean test(SimpleHttpExchange exchange, File file);

    /**
     * Determines the bytes to be sent back to the client based on their exchange and the file.
     *
     * @param exchange the http exchange from the client that is accessing the file
     * @param source file being referenced
     * @param bytes the file's bytes
     * @return the bytes being sent to the user
     */
    byte[] handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes);

}
