package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.logger.*;

import java.io.*;
import java.util.Objects;
import java.util.logging.*;

public final class LoggerService {

    private static final Logger logger = Logger.getLogger("WebDir / LoggerService");

    LoggerService(){
        final Logger logger = Logger.getLogger("Logger");
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.addHandler(
            new ConsoleHandler(){{
                setLevel(Level.INFO);
                setFormatter(new ConsoleLoggerFormatter());
            }}
        );
        try{ // log
            logger.addHandler(
                new FileHandler(System.currentTimeMillis() + ".log"){{
                    setLevel(Level.INFO);
                    setFormatter(new TimestampLoggerFormatter());
                }}
            );
        }catch(final IOException e){
            logger.severe("Failed to start log log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // latest
            logger.addHandler(
                new FileHandler("latest.log"){{
                    setLevel(Level.INFO);
                    setFormatter(new TimestampLoggerFormatter());
                }}
            );
        }catch(final IOException e){
            logger.severe("Failed to start latest log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // debug
            logger.addHandler(
                new FileHandler("debug.log"){{
                    setLevel(Level.ALL);
                    setFormatter(new TimestampDebugLoggerFormatter());
                }}
            );
        }catch(final IOException e){
            logger.severe("Failed to start debug log: " + '\n' + getStackTraceAsString(e));
        }

        logger.info("Finished initialization");
    }

    public static String getStackTraceAsString(final Throwable e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

}
