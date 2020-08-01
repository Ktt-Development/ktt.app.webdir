package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.logger.ILoggerService;
import com.kttdevelopment.webdir.generator.logger.LoggerFormatter;
import com.kttdevelopment.webdir.generator.object.Tuple3;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public final class LoggerService implements ILoggerService {

    private final List<Handler> handlers = new ArrayList<>();

    public LoggerService(){
        Logger logger = getLogger("Logger");
        logger.setLevel(Level.ALL);

        handlers.add(new ConsoleHandler(){{
            setLevel(Level.INFO);
            setFormatter(new LoggerFormatter(false,false));
        }} );

        @SuppressWarnings("unchecked")
        final Tuple3<String,Level,Formatter>[] loggers = new Tuple3[]{
            new Tuple3<>(System.currentTimeMillis() + ".log", Level.INFO, new LoggerFormatter(true, false)),
            new Tuple3<>("latest.log", Level.INFO, new LoggerFormatter(true, false)),
            new Tuple3<>("debug.log", Level.ALL, new LoggerFormatter(true, true))
        };

        for(final Tuple3<String,Level,Formatter> tuple : loggers){
            try{
                handlers.add( new FileHandler(tuple.getVar1()){{
                    setLevel(tuple.getVar2());
                    setFormatter(tuple.getVar3());
                }});
            }catch(final IOException | SecurityException e){
                logger.severe(String.format("Failed to start logger for %s \n %s", tuple.getVar1(), Exceptions.getStackTraceAsString(e)));
            }
        }
        logger = getLogger("Logger");

        logger.info("Finished logger service initialization");
    }

    @Override
    public final Logger getLogger(final String loggerName){
        final Logger logger = loggerName != null ? Logger.getLogger(loggerName) : Logger.getAnonymousLogger();
        final List<Handler> handlers = Arrays.asList(logger.getHandlers());
        for(final Handler handler : this.handlers)
            if(!handlers.contains(handler))
                logger.addHandler(handler);
        return logger;
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("LoggerService")
            .addObject("handlers",handlers)
            .toString();
    }

}
