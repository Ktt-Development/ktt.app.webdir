package com.kttdevelopment.webdir.generator;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.logger.LoggerFormatter;
import com.kttdevelopment.webdir.generator.object.TriTuple;

import java.io.IOException;
import java.util.logging.*;

public final class LoggerService {

    public LoggerService(){
        final Logger logger = Logger.getLogger("Logger");
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.addHandler( new ConsoleHandler(){{
            setLevel(Level.INFO);
            setFormatter(new LoggerFormatter(false,false));
        }} );

        @SuppressWarnings("unchecked")
        final TriTuple<String,Level,Formatter>[] loggers = new TriTuple[]{
            new TriTuple<>(System.currentTimeMillis() + ".log", Level.INFO, new LoggerFormatter(true,false)),
            new TriTuple<>("latest.log", Level.INFO, new LoggerFormatter(true,false)),
            new TriTuple<>("debug.log", Level.ALL, new LoggerFormatter(true,true))
        };

        for(final TriTuple<String,Level,Formatter> tuple : loggers){
            try{
                logger.addHandler( new FileHandler(tuple.getVar1()){{
                    setLevel(tuple.getVar2());
                    setFormatter(tuple.getVar3());
                }});
            }catch(final IOException | SecurityException e){
                logger.severe(String.format("Failed to start logger for %s \n %s", tuple.getVar1(), Exceptions.getStackTraceAsString(e)));
            }
        }

        logger.info("Finished logger service initialization");

    }

}
