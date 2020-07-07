package com.kttdevelopment.webdir.server.logger;

import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public class TimestampLoggerFormatter extends ConsoleLoggerFormatter {

    @SuppressWarnings("SpellCheckingInspection")
    private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    @Override
    public final String format(final LogRecord record){
        return '[' + time.format(record.getMillis() + ']' + ' ' + super.format(record));
    }

}
