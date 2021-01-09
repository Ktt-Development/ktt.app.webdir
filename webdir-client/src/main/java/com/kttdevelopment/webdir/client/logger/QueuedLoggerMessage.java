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

import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.logging.Level;

public final class QueuedLoggerMessage {

    private final String localizedLoggerKey, localeKey, defaultLoggerName, defaultMessage;
    private final Level level;
    private final Object[] args;

    public QueuedLoggerMessage(
        final String localizedLoggerKey,
        final String localeKey,
        final String defaultLoggerName,
        final String defaultMessage,
        final Level level,
        final Object[] args
    ){
        this.localizedLoggerKey = localizedLoggerKey;
        this.localeKey              = localeKey;
        this.defaultLoggerName      = defaultLoggerName;
        this.defaultMessage         = defaultMessage;
        this.level                  = level;
        this.args                   = args;
    }

    public final String getLocalizedLoggerKey(){
        return localizedLoggerKey;
    }

    public final String getLocaleKey(){
        return localeKey;
    }

    public final String getDefaultLoggerName(){
        return defaultLoggerName;
    }

    public final String getDefaultMessage(){
        return defaultMessage;
    }

    public final Level getLevel(){
        return level;
    }

    public final Object[] getArgs(){
        return args;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("localizedLoggerKey", localizedLoggerKey)
            .addObject("localeKey", localeKey)
            .addObject("defaultLoggerName", defaultLoggerName)
            .addObject("defaultMessage", defaultMessage)
            .addObject("level", level)
            .addObject("args", args)
            .toString();
    }

}
