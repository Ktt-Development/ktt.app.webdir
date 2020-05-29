package com.kttdevelopment.webdir;

import com.kttdevelopment.webdir.logger.LoggerFormatter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public final class LoggerService {

    public static final Logger logger = Logger.getGlobal();

    private final Formatter formatter = new LoggerFormatter();

    LoggerService(){
        final String prefix = "[Logger]" + ' ';

        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.addHandler(
            new ConsoleHandler(){{
                setLevel(Level.INFO);
                setFormatter(formatter);
            }}
        );
        try{ // log
            logger.addHandler(
                new FileHandler(System.currentTimeMillis() + ".log"){{
                    setLevel(Level.INFO);
                    setFormatter(formatter);
                }}
            );
        }catch(final IOException e){
            logger.severe(prefix + "Failed to start log log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // latest
            logger.addHandler(
                new FileHandler("latest.log"){{
                    setLevel(Level.INFO);
                    setFormatter(formatter);
                }}
            );
        }catch(final IOException e){
            logger.severe(prefix + "Failed to start latest log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // debug
            logger.addHandler(
                new FileHandler("debug.log"){{
                    setLevel(Level.ALL);
                    setFormatter(formatter);
                }}
            );
        }catch(final IOException e){
            logger.severe(prefix + "Failed to start debug log: " + '\n' + getStackTraceAsString(e));
        }

        logger.info(prefix + "Finished initialization");
    }

    public static String getStackTraceAsString(final Throwable e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

}
