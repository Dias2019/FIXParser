package org.parser.service.utils;

public class Validator {

    private static final char SOH = '\001';
    private final PatternMatcher HEADER_PATTERN;
    private final PatternMatcher CHECKSUM_PATTERN;

    private int bodyStart, bodyEnd;
    private int position;
    private int headerOffset;


    public Validator() {
        HEADER_PATTERN = new PatternMatcher("8=FIX.?.?" + SOH + "9=");
        CHECKSUM_PATTERN = new PatternMatcher("10=???" + SOH);
        reset();
    }


    public long validate(byte[] msg) {

        if (inValidHeader(msg) || inValidBody(msg) || inValidCheckSum(msg)) {
            reset();
            return -1;
        }

        long bodyStartEnd = (long) bodyStart << 32 | bodyEnd;
        reset();
        return bodyStartEnd;
    }

    private boolean inValidHeader(byte[] msg) {

        long headerPos = HEADER_PATTERN.find(msg, position);
        if (headerPos == -1L)
            return true;
        headerOffset = (int) headerPos;
        int headerLength = (int) (headerPos >>> 32);
        position = headerOffset + headerLength;
        return false;
    }

    private boolean inValidBody(byte[] msg) {

        byte ch = 0;
        int bodyLength = 0;
        while (position < msg.length) {
            ch = msg[position++];
            if (ch < '0' || ch > '9')
                break;
            bodyLength = bodyLength * 10 + (ch - '0');
        }
        if (ch != SOH || msg.length - position < bodyLength)
            return true;
        bodyStart = position;
        position += bodyLength;
        bodyEnd = position-1;
        return false;
    }

    private boolean inValidCheckSum(byte[] msg) {

        return CHECKSUM_PATTERN.match(msg, position) < 0 || calculateChecksum(msg) != parseCheckSum(msg);
    }

    private int parseCheckSum(byte[] msg) {

        int checkSumValue = 0;
        for (int i = position + 3; i < msg.length - 1; i++) {
            checkSumValue = checkSumValue * 10 + (msg[i] - '0');
        }
        return checkSumValue;
    }

    private int calculateChecksum(byte[] msg) {

        int sum = 0;
        for (int i = headerOffset; i < position; i++) {
            sum += (msg[i] & 0xFF);
        }
        return sum & 0xFF;
    }


    private void reset() {
        position = 0;
        bodyStart = 0;
        bodyEnd = 0;
    }

}
