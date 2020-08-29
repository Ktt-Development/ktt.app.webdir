package com.kttdevelopment.webdir.client.logger;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.logging.Level;

public final class QueuedLoggerMessage {
    
    private final String localizedLogger, key, defaultLogger, defaultMessage;
    private final Level level;
    private final Object[] args;
    
    public QueuedLoggerMessage(final String localizedLogger, final String key, final String defaultLogger, final String defaultMessage, final Level level, final Object[] args){
        this.localizedLogger    = localizedLogger;
        this.key                = key;
        this.defaultLogger      = defaultLogger;
        this.defaultMessage     = defaultMessage;
        this.level              = level;
        this.args               = args;
    }

    public final String getLocalizedLogger(){
        return localizedLogger;
    }

    public final String getKey(){
        return key;
    }

    public final String getDefaultLogger(){
        return defaultLogger;
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
            .addObject("localizedLogger",localizedLogger)
            .addObject("key",key)
            .addObject("defaultLogger",defaultLogger)
            .addObject("defaultMessage",defaultMessage)
            .addObject("level",level)
            .addObject("args",args)
            .toString();
    }

}
