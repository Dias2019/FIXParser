package org.parser.service.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternMatcherTest {

    @Test
    public void testFindPattern() {
        PatternMatcher matcher = new PatternMatcher("FIX");
        String inputMsg = "8=FIX.4.2|9=12|35=A|".replace('|', '\001');

        long result = matcher.find(inputMsg.getBytes(), 0);
        int position = (int) result;
        int length = (int) (result >>> 32);

        assertEquals(2, position, "Pattern should start at position 2");
        assertEquals(3, length, "Pattern length should be 3");
    }

    @Test
    public void testFindPatternNotFound() {
        PatternMatcher matcher = new PatternMatcher("FIX.4.4");
        String inputMsg = "8=FIX.4.2|9=12|35=A|".replace('|', '\001');

        long result = matcher.find(inputMsg.getBytes(), 0);
        assertEquals(-1L, result, "Pattern should not be found");
    }
}