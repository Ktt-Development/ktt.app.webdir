package com.kttdevelopment.webdir;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public final class Logger {

    public static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

    private final Formatter formatter = new Formatter();

    Logger(){
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
            logger.severe("Failed to start log log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // latest
            logger.addHandler(
                new FileHandler("latest.log"){{
                    setLevel(Level.INFO);
                    setFormatter(formatter);
                }}
            );
        }catch(final IOException e){
            logger.severe("Failed to start latest log: " + '\n' + getStackTraceAsString(e));
        }

        try{ // debug
            logger.addHandler(
                new FileHandler("debug.log"){{
                    setLevel(Level.ALL);
                    setFormatter(formatter);
                }}
            );
        }catch(final IOException e){
            logger.severe("Failed to start debug log: " + '\n' + getStackTraceAsString(e));
        }

        logger.info("[Logger] Finished initialization");
    }

    private static final class Formatter extends java.util.logging.Formatter {

        @SuppressWarnings("SpellCheckingInspection")
        private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

        @Override
        public final String format(final LogRecord record){
            return String.format("[%s] [%s] [%s#%s@%s] %s \n",time.format(record.getMillis()),record.getLevel(),record.getSourceClassName(),record.getSourceMethodName(),record.getThreadID(), record.getMessage());
        }
    }

    public static String getStackTraceAsString(final Exception e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

}
