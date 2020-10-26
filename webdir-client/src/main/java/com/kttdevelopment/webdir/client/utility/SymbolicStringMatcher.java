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
