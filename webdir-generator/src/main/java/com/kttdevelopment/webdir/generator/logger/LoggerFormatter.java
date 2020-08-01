package com.kttdevelopment.webdir.generator.logger;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LoggerFormatter extends Formatter {

    private final boolean hasTimestamp;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private final boolean hasTrace;
    private static final String trace = "%s@%s#%s";

    private static final String name = "%s >";

    public LoggerFormatter(final boolean hasTimestamp, final boolean hasTrace){
        this.hasTimestamp = hasTimestamp;
        this.hasTrace = hasTrace;
    }

    @SuppressWarnings("unused") // this is to prevent locale files from listing these as unused
    private static final String[] levelKeys = new String[]{
        "logger.level.SEVERE",
        "logger.level.WARNING",
        "logger.level.INFO",
        "logger.level.CONFIG",
        "logger.level.FINE",
        "logger.level.FINER",
        "logger.level.FINEST",
    };

    @Override
    public final String format(final LogRecord record){
        final String level = record.getLevel().getName().toUpperCase();
        return
            (hasTimestamp ? '[' + sdf.format(record.getMillis()) + ']' + ' ' : "") +
            '[' + Exceptions.requireNonExceptionElse(() -> Objects.requireNonNull(Vars.Main.getLocaleService().getString("logger.level." + level)), level) + ']' + ' ' +
            (hasTrace ? '[' + String.format(trace,record.getThreadID(),record.getSourceClassName(),record.getSourceMethodName()) + ']' + ' ' : "") +
            String.format(name,record.getLoggerName()) + ' ' +
            record.getMessage() + '\n';
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(o == null || getClass() != o.getClass())
            return false;
        final LoggerFormatter other = (LoggerFormatter) o;
        return hasTimestamp == other.hasTimestamp &&
               hasTrace == other.hasTrace;
    }

    @Override
    public String toString(){
        return new toStringBuilder("LoggerFormatter")
            .addObject("hasTimestamp",hasTimestamp)
            .addObject("timestampSDF",sdf.toPattern())
            .addObject("hasTrace",hasTrace)
            .addObject("traceString",trace)
            .addObject("nameString",name)
            .toString();
    }

}
