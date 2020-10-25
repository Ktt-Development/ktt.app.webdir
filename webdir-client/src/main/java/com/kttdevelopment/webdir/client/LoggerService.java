package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.logger.LoggerFormatter;
import com.kttdevelopment.webdir.client.logger.QueuedLoggerMessage;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;

public final class LoggerService {

    private final List<QueuedLoggerMessage> queuedMessages = new ArrayList<>();

    private synchronized void addQueuedLoggerMessageSafe(final String localizedLoggerName, final String localeKey, final String defaultLoggerName, final String defaultMessage, final Level level, final Object... args){
        queuedMessages.add(new QueuedLoggerMessage(localizedLoggerName, localeKey, defaultLoggerName, defaultMessage, level, args));
        Logger.getLogger(defaultLoggerName).log(level, String.format(defaultMessage, args));
    }

    public synchronized final void addQueuedLoggerMessage(final String localizedLoggerName, final String localeKey, final String defaultLoggerName, final String defaultMessage, final Level level, final Object... args){
        queuedMessages.add(new QueuedLoggerMessage(localizedLoggerName, localeKey, defaultLoggerName, defaultMessage, level, args));
        getLogger(defaultLoggerName).log(level, String.format(defaultMessage, args));
    }

    public final List<QueuedLoggerMessage> getQueuedLoggerMessages(){
        return Collections.unmodifiableList(queuedMessages);
    }

    private final List<Handler> handlers = new ArrayList<>();

    LoggerService() throws IOException{
        Logger logger = Logger.getLogger("Logger Service");
        logger.setLevel(Level.ALL);

        // todo: start message (use safe to avoid volatile class state)

        handlers.add(new ConsoleHandler(){{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false, false));
        }});
        // todo: add add message
        handlers.add(new FileHandler(FileUtility.getFreeFile(new File(System.currentTimeMillis() + ".log")).getName()){{
            setLevel(Level.INFO);
            setFormatter(new LoggerFormatter(true, false));
        }});
        // todo: add add message
        handlers.add(new FileHandler("latest.log"){{
            setLevel(Level.INFO);
            setFormatter(new LoggerFormatter(true, false));
        }});
        // todo: add add message
        handlers.add(new FileHandler( "debug.log"){{
            setLevel(Level.INFO);
            setFormatter(new LoggerFormatter(true, true));
        }});
        // todo: add add message

        this.handlerCount = handlers.size();

        // todo: add finished message
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
        return ExceptionUtility.getStackTraceAsString(e);
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
