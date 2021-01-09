/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client.logger;

import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LoggerFormatter extends Formatter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz");

    private static final String trace = "%s@%s#%s", name = "%s >";

    private final boolean hasTimestamp, hasTrace;

    public LoggerFormatter(final boolean hasTimestamp, final boolean hasTrace){
        this.hasTimestamp = hasTimestamp;
        this.hasTrace     = hasTrace;
    }

    @Override
    public final String format(final LogRecord record){
        final String level = ExceptionUtility.requireNonExceptionElse(() -> Main.getLocale().getString("logger.level." + record.getLevel().getName().toUpperCase()), record.getLevel().getName().toUpperCase());
        return
            (hasTimestamp ? '[' + sdf.format(record.getMillis()) + ']' + ' ' : "") +
            '[' + level + ']' + ' ' +
            (hasTrace ? '[' + String.format(trace,record.getThreadID(),record.getSourceClassName(),record.getSourceMethodName()) + ']' + ' ' : "") +
            String.format(name, record.getLoggerName()) + ' ' +
            record.getMessage() + '\n';
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final LoggerFormatter that = (LoggerFormatter) o;
        return hasTimestamp == that.hasTimestamp &&
               hasTrace == that.hasTrace;
    }

    @Override
    public String toString(){
        return new ToStringBuilder("LoggerFormatter")
            .addObject("timestampSDF", sdf.toPattern())
            .addObject("traceString", trace)
            .addObject("nameString", name)
            .addObject("hasTimestamp", hasTimestamp)
            .addObject("hasTrace", hasTrace)
            .toString();
    }

}
