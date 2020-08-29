package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.webdir.client.logger.LoggerFormatter;
import com.kttdevelopment.webdir.client.logger.QueuedLoggerMessage;
import com.kttdevelopment.webdir.client.object.Tuple3;

import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

public final class LoggerService {

    // Queued messages to run when locale is loaded

    private final List<QueuedLoggerMessage> queuedMessages = new ArrayList<>();

    public final void addQueuedLoggerMessage(final String localizedLogger, final String key, final String defaultLogger, final String defaultMessage, final Level level, final Object... args){
        queuedMessages.add(new QueuedLoggerMessage(localizedLogger, key, defaultLogger, defaultMessage, level, args));
        getLogger(defaultLogger).log(level, String.format(defaultMessage, args));
    }

    public final List<QueuedLoggerMessage> getQueuedLoggerMessages(){
        return Collections.unmodifiableList(queuedMessages);
    }

    //

    private final List<Handler> handlers = new ArrayList<>();

    public LoggerService(){
        Logger logger = Logger.getLogger("Logger Service");
        logger.setLevel(Level.ALL);

        addQueuedLoggerMessage(
            "loggerService","loggerService.const.started",
            logger.getName(),"Started logger service initialization",
            Level.INFO
        );

        handlers.add(new ConsoleHandler(){{
            setLevel(Level.ALL);
            setFormatter(new LoggerFormatter(false,false));
        }});

        @SuppressWarnings("unchecked")
        final Tuple3<String,Level,Formatter>[] loggers = new Tuple3[]{
            new Tuple3<>(System.currentTimeMillis() + ".log", Level.INFO, new LoggerFormatter(true, false)),
            new Tuple3<>("latest.log",Level.INFO,new LoggerFormatter(true,false)),
            new Tuple3<>("debug.log",Level.ALL,new LoggerFormatter(true,true))
        };

        for(final Tuple3<String,Level,Formatter> tuple : loggers){
            try{
                final Handler handler = new FileHandler(tuple.getVar1()){{
                    setLevel(tuple.getVar2());
                    setFormatter(tuple.getVar3());
                }};
                handlers.add(handler);
                addQueuedLoggerMessage(
                    "loggerService","loggerService.const.addHandler",
                    logger.getName(),"Added logger handler %s",
                    Level.FINE,handler
                );
            }catch(final Throwable e){
                addQueuedLoggerMessage(
                    "loggerService","loggerService.const.failedAddHandler",
                    logger.getName(),"Failed to start logger for %s \n %s",
                    Level.SEVERE,getStackTraceAsString(e)
                );
            }
        }

        this.handlerCount = handlers.size();

        addQueuedLoggerMessage(
            "loggerService","loggerService.const.finished",
            logger.getName(),"Finished logger service initialization",
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
            if(!handlers.contains(handler))
                logger.addHandler(handler);
        return logger;
    }

    public static String getStackTraceAsString(final Throwable e){
        return ExceptionUtil.getStackTraceAsString(e);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("handlers",handlers)
            .addObject("queuedLoggerMessages", queuedMessages)
            .toString();
    }

}
