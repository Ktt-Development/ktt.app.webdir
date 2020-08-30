package com.kttdevelopment.webdir.client.function;

import java.util.regex.Pattern;

/**
 * This class tests strings where <code>*</code> means any and <code>!</code> is absolute negation.
 */
public abstract class SymbolicStringMatcher {

    private static final String regex = "^\\Q%s\\E$";

    private static boolean matchesAsterisk(final String format, final String string){ // file names prohibit '*' from being used so an escape character is not needed
        return Pattern.compile(String.format(regex,format.replace("*","\\E.*\\Q"))).matcher(string).matches();
    }

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
