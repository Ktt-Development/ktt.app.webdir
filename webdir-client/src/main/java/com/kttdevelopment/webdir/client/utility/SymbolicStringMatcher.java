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

package com.kttdevelopment.webdir.client.utility;

import java.util.regex.Pattern;

public abstract class SymbolicStringMatcher {

    private static boolean matchesAsterisk(final String format, final String string){
        // quote method must be used to make sure regex isn't injected
        StringBuilder regex = new StringBuilder("^");
        for(final String s : format.split("\\*"))
            regex.append(!s.isEmpty() ? Pattern.quote(s) : "").append(".*");

        regex.append('$');
        return Pattern.compile(regex.toString()).matcher(string).matches();
    }

    // don't use ternary here (too unreadable)
    public static MatchState matches(final String format, final String string){
        if(format.equals('!' + string) || (format.startsWith("!") && matchesAsterisk(format.substring(1),string)))
            return MatchState.NEGATIVE_MATCH;
        else
            return format.equals(string) || matchesAsterisk(format,string) ? MatchState.MATCH : MatchState.NO_MATCH;
    }

    public enum MatchState {
        MATCH,
        NO_MATCH,
        NEGATIVE_MATCH
    }

}
