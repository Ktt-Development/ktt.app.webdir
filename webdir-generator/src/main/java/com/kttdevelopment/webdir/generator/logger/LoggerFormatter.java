package com.kttdevelopment.webdir.generator.logger;

import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.function.Exceptions;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {

    private final boolean hasTimestamp;
    @SuppressWarnings("SpellCheckingInspection")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private final boolean hasTrace;
    private static final String trace = "%s@%s#%s";

    private static final String name = "%s >";

    public LoggerFormatter(final boolean hasTimestamp, final boolean hasTrace){
        this.hasTimestamp = hasTimestamp;
        this.hasTrace = hasTrace;
    }

    @Override
    public final String format(final LogRecord record){
        final String level = record.getLevel().getName().toUpperCase();

        return
            (hasTimestamp ? '[' + sdf.format(record.getMillis()) + ']' + ' ' : "") +
            '[' + Exceptions.requireNonExceptionElse(() -> Objects.requireNonNull(Main.getLocaleService().getString("logger.level." + level)), level) + ']' + ' ' +
            (hasTrace ? '[' + String.format(trace,record.getThreadID(),record.getSourceClassName(),record.getSourceMethodName()) + ']' + ' ' : "") +
            String.format(name,record.getLoggerName()) + ' ' +
            record.getMessage() + '\n';
    }

}
