/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.logger.LoggerFormatter;
import com.kttdevelopment.webdir.client.logger.QueuedLoggerMessage;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

public final class LoggerService {

    private final List<QueuedLoggerMessage> queuedMessages = new ArrayList<>();

    @SuppressWarnings("SameParameterValue")
    private synchronized void addQueuedLoggerMessageSafe(final String localizedLoggerKey, final String localeKey, final String defaultLoggerName, final String defaultMessage, final Level level, final Object... args){
        queuedMessages.add(new QueuedLoggerMessage(localizedLoggerKey, localeKey, defaultLoggerName, defaultMessage, level, args));
        Logger.getLogger(defaultLoggerName).log(level, String.format(defaultMessage, args));
    }

    public synchronized final void addQueuedLoggerMessage(final String localizedLoggerKey, final String localeKey, final String defaultLoggerName, final String defaultMessage, final Level level, final Object... args){
        queuedMessages.add(new QueuedLoggerMessage(localizedLoggerKey, localeKey, defaultLoggerName, defaultMessage, level, args));
        getLogger(defaultLoggerName).log(level, String.format(defaultMessage, args));
    }

    public final List<QueuedLoggerMessage> getQueuedLoggerMessages(){
        return queuedMessages;
    }

    private final List<Handler> handlers = new ArrayList<>();

    LoggerService(){
        final String loggerName = "Logger Service";
        Logger.getGlobal().setUseParentHandlers(false);

        addQueuedLoggerMessageSafe(
            "logger.name", "logger.constructor.start",
            loggerName, "Started logger service initialization.",
            Level.INFO
        );

        // console handler
        {
            handlers.add(new ConsoleHandler() {{
                setLevel(Level.ALL);
                setFormatter(new LoggerFormatter(false, false));
            }});

            addQueuedLoggerMessageSafe(
                "logger.name", "logger.constructor.console_log",
                loggerName, "Added console log to logger.",
                Level.FINE
            );
        }
        // latest handler
            {
            final String log = "latest.log";
            try{
                handlers.add(new FileHandler(log) {{
                    setLevel(Level.INFO);
                    setFormatter(new LoggerFormatter(true, false));
                }});
                addQueuedLoggerMessageSafe(
                    "logger.name", "logger.constructor.log.success",
                    loggerName, "Started logging to file %s.",
                    Level.FINE, log
                );
            }catch(final IOException e){
                addQueuedLoggerMessageSafe(
                    "logger.name", "logger.constructor.log.fail",
                    loggerName, "Failed to start logging for file %s. %s",
                    Level.SEVERE, log, getStackTraceAsString(e)
                );
            }
        }
        // debug handler
        {
            final String log = "debug.log";
            try{
                handlers.add(new FileHandler(log) {{
                    setLevel(Level.ALL);
                    setFormatter(new LoggerFormatter(true, true));
                }});
                addQueuedLoggerMessageSafe(
                    "logger.name", "logger.constructor.log.success",
                    loggerName, "Started logging to file %s.",
                    Level.FINE, log
                );
            }catch(final IOException e){
                addQueuedLoggerMessageSafe(
                    "logger.name", "logger.constructor.log.fail",
                    loggerName, "Failed to start logging for file %s. %s",
                    Level.SEVERE, log, getStackTraceAsString(e)
                );
            }
        }

        this.handlerCount = handlers.size();

        addQueuedLoggerMessageSafe(
            "logger.name", "logger.constructor.finish",
            loggerName, "Finished logger service initialization.",
            Level.INFO
        );
    }

    private final int handlerCount;

    public final Logger getLogger(final String loggerName){
        final Logger logger = loggerName != null ? Logger.getLogger(loggerName) : Logger.getAnonymousLogger();
        logger.setLevel(Level.ALL);

        if(logger.getHandlers().length == handlerCount) return logger; // skip check if already added

        final List<Handler> handlers = Arrays.asList(logger.getHandlers());
        for(final Handler handler : this.handlers)
            synchronized(LoggerService.this){
                if(!handlers.contains(handler))
                    logger.addHandler(handler);
            }
        return logger;
    }

    public static String getStackTraceAsString(final Throwable e){
        return "\n---- [ Stack Trace ] ----\n" + ExceptionUtility.getStackTraceAsString(e) + "\n---- [ End Stack Trace ] ----";
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("handlers", handlers)
            .addObject("queuedLoggerMessages", queuedMessages)
            .toString();
    }

}
