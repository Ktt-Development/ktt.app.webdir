package com.kttdevelopment.webdir.logger;

import com.kttdevelopment.webdir.Application;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LoggerFormatter extends Formatter {

    @SuppressWarnings("SpellCheckingInspection")
    private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private static final String format = "[%s] [%s] [%s#%s@%s] %s %n"; // time, scope, message

    @Override
    public final String format(final LogRecord record){
        final String level = record.getLevel().getName();
        return String.format(
            format,
            time.format(record.getMillis()),
            Objects.requireNonNullElse(Application.locale.getString("logger.level." + level),level),
            record.getSourceClassName(),
                /* # */ record.getSourceMethodName(),
                /* @ */ record.getThreadID(),
            record.getMessage()
        );
    }

}
