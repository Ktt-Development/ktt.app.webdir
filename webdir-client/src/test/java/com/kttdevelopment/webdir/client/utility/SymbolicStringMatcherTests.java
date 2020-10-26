package com.kttdevelopment.webdir.client.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.kttdevelopment.webdir.client.utility.SymbolicStringMatcher.*;
import static com.kttdevelopment.webdir.client.utility.SymbolicStringMatcher.MatchState.*;

public class SymbolicStringMatcherTests {

    @Test
    public void test(){
        // positive matches
        Assertions.assertEquals(MATCH, matches("OK", "OK"));
        Assertions.assertEquals(MATCH, matches("*\\E\\d\\Q", "\\E\\d\\Q"));
        Assertions.assertEquals(MATCH, matches("*O*K*", "OK"));
        Assertions.assertEquals(MATCH, matches("*O*", "OK"));
        Assertions.assertEquals(MATCH, matches("*K*", "OK"));

        // no match
        Assertions.assertEquals(NO_MATCH, matches("NO", "OK"));
        Assertions.assertEquals(NO_MATCH, matches("*NO*", "OK"));

        // negative match
        Assertions.assertEquals(NEGATIVE_MATCH, matches("!OK", "OK"));
        Assertions.assertEquals(NEGATIVE_MATCH, matches("!*\\E\\d\\Q", "\\E\\d\\Q"));
        Assertions.assertEquals(NEGATIVE_MATCH, matches("!*O*K*", "OK"));
        Assertions.assertEquals(NEGATIVE_MATCH, matches("!*O*", "OK"));
        Assertions.assertEquals(NEGATIVE_MATCH, matches("!*K*", "OK"));
    }

}
