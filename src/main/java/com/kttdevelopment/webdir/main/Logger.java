package com.kttdevelopment.webdir.main;

import java.text.SimpleDateFormat;
import java.util.logging.*;

import static com.kttdevelopment.webdir.main._vars.*;

public abstract class Logger {

    abstract static class Main {

        synchronized static void init(){
            logger.logger.setLevel(logger.level);
            logger.logger.addHandler(
                new ConsoleHandler(){{
                    setLevel(logger.level);
                    setFormatter(logger.formatter);
                }}
            );

            // todo: add file handler
            logger.logger.setUseParentHandlers(false);
        }

    }

    static final class Formatter extends java.util.logging.Formatter {

        static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

        @Override
        public final String format(final LogRecord record){
            return String.format("[%s] [%s] [%s#%s@%s] %s \n",time.format(record.getMillis()),record.getLevel(),record.getSourceClassName(),record.getSourceMethodName(),record.getThreadID(), record.getMessage());
        }

    }

}
