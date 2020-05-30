package com.kttdevelopment.webdir.logger;

import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public final class TimestampLoggerFormatter extends ConsoleLoggerFormatter {

    @SuppressWarnings("SpellCheckingInspection")
    private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private static final String format = "[%s] [%s] [%s#%s@%s] %s %n"; // time, scope, message

    @Override
    public final String format(final LogRecord record){
        return '[' + time.format(record.getMillis() + ']' + ' ' + super.format(record));
    }

}
