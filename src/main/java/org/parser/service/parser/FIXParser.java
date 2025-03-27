package org.parser.service.parser;

import org.parser.client.IMessageConsumer;
import org.parser.service.parser.message.FIXMessage;
import org.parser.service.parser.tag_mapper.tag.FIXTag;
import org.parser.service.parser.tag_mapper.IFIXTagTransformer;
import org.parser.service.utils.Validator;

public class FIXParser {

    private static final byte SOH = 1;
    private final Validator validator;
    private final IFIXTagTransformer tagMapper;


    public FIXParser(IFIXTagTransformer tagMapper) {
        validator = new Validator();
        this.tagMapper = tagMapper;
    }


    public boolean parse(final byte[] msg, final IMessageConsumer consumer) {

        long isValid = validator.validate(msg);
        if (isValid == -1L)
            return false;

        FIXMessage message = new FIXMessage();
        int bodyEnd = (int) isValid, bodyStart = (int) (isValid >>> 32);
        while (bodyStart < bodyEnd) {

            int fieldEnd = find(msg, SOH, bodyStart, msg.length);
            int tagStart = bodyStart, tagEnd = find(msg, (byte)'=', bodyStart, fieldEnd);
            int tag = parseFIXTag(msg, tagStart, tagEnd);
            String value = new String(msg, tagEnd+1, fieldEnd-(tagEnd+1));

            FIXTag fixTag;
            if ((fixTag = tagMapper.getFixTagFromNumber(tag)) == null)
                return false;

            if (fixTag.hasEnumOptions() && !fixTag.hasEnum(value))
                return false;

            message.addTag(fixTag, value);

            bodyStart = fieldEnd + 1;
        }

        consumer.accept(message);
        return true;
    }

    private int find(byte[] msg, byte target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (msg[i] == target)
                return i;
        }
        return -1;
    }

    private int parseFIXTag(byte[] msg, int tagStart, int tagEnd) {
        int tag = 0;
        for (int i = tagStart; i < tagEnd; i++) {
            tag = (tag * 10 + (msg[i] - '0'));
        }
        return tag;
    }
}
