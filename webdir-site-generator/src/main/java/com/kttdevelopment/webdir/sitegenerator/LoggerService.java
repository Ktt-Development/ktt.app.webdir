package com.kttdevelopment.webdir.sitegenerator;

import com.kttdevelopment.webdir.sitegenerator.function.Exceptions;
import com.kttdevelopment.webdir.sitegenerator.object.TriTuple;

import java.io.IOException;
import java.util.logging.*;

public final class LoggerService {

    LoggerService(){
        final Logger logger = Logger.getLogger("Logger");
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.addHandler( new ConsoleHandler(){{
            setLevel(Level.INFO);
            setFormatter(null);
        }} );

        @SuppressWarnings("unchecked")
        final TriTuple<String,Level,Formatter>[] loggers = new TriTuple[]{
            new TriTuple<>(System.currentTimeMillis() + ".log", Level.INFO, null),
            new TriTuple<>("latest.log", Level.INFO, null),
            new TriTuple<>("debug.log", Level.ALL, null)
        };

        for(final TriTuple<String, Level, Formatter> tuple : loggers){
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
