package org.parser.service.utils;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

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
        PatternMatcher matcher = new PatternMatcher("FIX");
        String inputMsg = "8=FIX.4.2|9=12|35=A|".replace('|', '\001');

        long result = matcher.find(inputMsg.getBytes(), 0);
        assertEquals(-1L, result, "Pattern should not be found");
    }

//    @Test
//    public void testMatchPattern() {
//        PatternMatcher matcher = new PatternMatcher("35=A");
//        ByteBuffer buffer = ByteBuffer.wrap("8=FIX.4.2|9=12|35=A|".replace('|', '\001').getBytes());
//
//        int matchLength = matcher.match(buffer, 15);
//
//        assertEquals(4, matchLength, "Pattern should match with length 4");
//    }
//
//    @Test
//    public void testMatchPatternNotFound() {
//        PatternMatcher matcher = new PatternMatcher("35=B");
//        ByteBuffer buffer = ByteBuffer.wrap("8=FIX.4.2|9=12|35=A|".replace('|', '\001').getBytes());
//
//        int matchLength = matcher.match(buffer, 15);
//
//        assertEquals(0, matchLength, "Pattern should not match");
//    }
//
//    @Test
//    public void testGetLength() {
//        PatternMatcher matcher = new PatternMatcher("35=A");
//
//        int minLength = matcher.getLength();
//
//        assertEquals(4, minLength, "Minimum length should be 4");
//    }
}