package com.kttdevelopment.webdir.client.logger;

import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.logging.Level;

public final class QueuedLoggerMessage {

    private final String localizedLoggerName, localeKey, defaultLoggerName, defaultMessage;
    private final Level level;
    private final Object[] args;

    public QueuedLoggerMessage(
        final String localizedLoggerName,
        final String localeKey,
        final String defaultLoggerName,
        final String defaultMessage,
        final Level level,
        final Object[] args
    ){
        this.localizedLoggerName    = localizedLoggerName;
        this.localeKey              = localeKey;
        this.defaultLoggerName      = defaultLoggerName;
        this.defaultMessage         = defaultMessage;
        this.level                  = level;
        this.args                   = args;
    }

    public final String getLocalizedLoggerName(){
        return localizedLoggerName;
    }

    public final String getLocaleKey(){
        return localeKey;
    }

    public final String getDefaultLoggerName(){
        return defaultLoggerName;
    }

    public final String getDefaultMessage(){
        return defaultMessage;
    }

    public final Level getLevel(){
        return level;
    }

    public final Object[] getArgs(){
        return args;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("localizedLoggerName", localizedLoggerName)
            .addObject("localeKey", localeKey)
            .addObject("defaultLoggerName", defaultLoggerName)
            .addObject("defaultMessage", defaultMessage)
            .addObject("level", level)
            .addObject("args", args)
            .toString();
    }

}
