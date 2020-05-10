package com.kttdevelopment.webdir;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public abstract class Logger {

    public static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
    static final Formatter formatter = new Formatter();

    private static boolean init = false;

    public synchronized static void main(){
        if(init) return; init = true;

        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.addHandler(
            new ConsoleHandler(){{
                setLevel(Level.ALL);
                setFormatter(formatter);
            }}
        );

        /*
        try{ // log
            logger.addHandler(
                new FileHandler(){{
                    setLevel(Level.INFO);
                    setFormatter(formatter);
                }}
            );
        }catch(IOException e){

        }

        try{ // latest
            logger.addHandler(
                new FileHandler(){{
                    setLevel(Level.INFO);
                    setFormatter(formatter);
                }}
            );
        }catch(IOException e){

        }

        try{ // debug
            logger.addHandler(
                new FileHandler(){{
                    setLevel(Level.ALL);
                    setFormatter(formatter);
                }}
            );
        }catch(IOException e){

        }
        */
        logger.info("Logger started");
    }

    static final class Formatter extends java.util.logging.Formatter {

        @SuppressWarnings("SpellCheckingInspection")
        private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

        @Override
        public final String format(final LogRecord record){
            return String.format("[%s] [%s] [%s#%s@%s] %s \n",time.format(record.getMillis()),record.getLevel(),record.getSourceClassName(),record.getSourceMethodName(),record.getThreadID(), record.getMessage());
        }

    }

//

    public static String getStackTraceAsString(final Exception e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

}
