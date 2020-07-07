package com.kttdevelopment.webdir.server.logger;

import com.kttdevelopment.webdir.server.Application;

import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleLoggerFormatter extends Formatter {

    private static final String format = "[%s] [%s#%s@%s] %s %s %n"; // time, scope, message

    @Override
    public String format(final LogRecord record){
        final String level = record.getLevel().getName();

        return String.format(
            format,
            Objects.requireNonNullElse(Application.getLocaleService().getString("logger.level." + level), level),
            record.getSourceClassName(),
                /* # */ record.getSourceMethodName(),
                /* @ */ record.getThreadID(),
            Objects.requireNonNullElse( record.getLoggerName() + '⏵' + ' ',""),
            record.getMessage()
        );
    }

}
