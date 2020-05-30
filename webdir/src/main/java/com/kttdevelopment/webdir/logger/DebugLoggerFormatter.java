package com.kttdevelopment.webdir.logger;

import java.util.logging.LogRecord;

public class DebugLoggerFormatter extends ConsoleLoggerFormatter {

    private static final String format = "[%s#%s@%s]";

    @Override
    public String format(final LogRecord record){
        final String trace = String.format(
            format,
            record.getSourceClassName(),
            /* # */ record.getSourceMethodName(),
            /* @ */ record.getThreadID()
        );

        return trace + ' ' + super.format(record);
    }

}
