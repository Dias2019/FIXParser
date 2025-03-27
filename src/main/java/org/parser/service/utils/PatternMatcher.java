package org.parser.service.utils;

public class PatternMatcher {

    private final byte[] pattern;


    public PatternMatcher(String stringPattern) {
        pattern = stringPattern.getBytes();
    }


    public long find(byte[] msg, int offset) {

        int length;
        byte first = pattern[0];
        for (int limit = msg.length - pattern.length + 1; offset < limit; offset++) {
            if (msg[offset] == first && (length = match(msg, offset)) > 0) {
                return (long) length << 32 | offset;
            }
        }
        return -1L;
    }


    public int match(byte[] msg, int offset) {

        final int startOffset = offset;
        int patternOffset = 0;
        for (int msgLimit = msg.length; patternOffset < pattern.length && offset < msgLimit;
             patternOffset++, offset++) {

            if (msg[offset] != pattern[patternOffset] && pattern[patternOffset] != '?')
                return -1;
        }

        if (patternOffset != pattern.length)
            return -1;

        return offset - startOffset;
    }
}
