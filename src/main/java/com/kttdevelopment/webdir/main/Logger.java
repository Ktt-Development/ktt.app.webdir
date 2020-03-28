package com.kttdevelopment.webdir.main;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public abstract class Logger {

    public static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
    static Level level = Level.ALL;
    static final Formatter formatter = new com.kttdevelopment.webdir.main.Logger.Formatter();

    public synchronized static void setLevel(final Level level){
        Logger.level = level;
        for(final Handler handler : logger.getHandlers())
            handler.setLevel(level);
        logger.info(Locale.getString("logging.changedLevel"));
    }

    abstract static class Main {

        // do not use locale here (locale not set yet)
        synchronized static void init(){
            logger.setLevel(level);
            logger.addHandler(
                new ConsoleHandler(){{
                    setLevel(level);
                    setFormatter(formatter);
                }}
            );

            try{
                logger.addHandler(
                    new FileHandler(com.kttdevelopment.webdir.main.Main.root + System.currentTimeMillis() + ".log") {{
                        setLevel(level);
                        setFormatter(formatter);
                    }}
                );
            }catch(final IOException e){
                logger.severe("Failed to start logging for log file." + '\n' + getStackTraceAsString(e));
            }
            try{
                logger.addHandler(
                    new FileHandler(com.kttdevelopment.webdir.main.Main.root + "latest.log"){{
                        setLevel(level);
                        setFormatter(formatter);
                    }}
                );
            }catch(IOException e){
                logger.severe("Failed to start logging for latest log file." + '\n' + getStackTraceAsString(e));
            }

            logger.setUseParentHandlers(false);

            logger.info("Logger started");
        }

    }

    static final class Formatter extends java.util.logging.Formatter {

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
