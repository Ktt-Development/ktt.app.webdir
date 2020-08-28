package com.kttdevelopment.webdir.client;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.webdir.client.logger.LoggerFormatter;
import com.kttdevelopment.webdir.client.object.Tuple3;

import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

public final class LoggerService {

    private final List<Handler> handlers = new ArrayList<>();

    public LoggerService(){
        Logger logger = Logger.getLogger("logger");
        logger.setLevel(Level.ALL);

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
                handlers.add(new FileHandler(tuple.getVar1()){{
                    setLevel(tuple.getVar2());
                    setFormatter(tuple.getVar3());
                }});
            }catch(final Throwable e){
                logger.severe(String.format("Failed to start logger for %s \n %s", tuple.getVar1(), getStackTraceAsString(e)));
            }
        }

        logger = getLogger("Logger");
        logger.info("Finished logger service initialization");
    }

    public final Logger getLogger(final String loggerName){
        final Logger logger = loggerName != null ? Logger.getLogger(loggerName) : Logger.getAnonymousLogger();
        logger.setLevel(Level.ALL);

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
            .toString();
    }

}
