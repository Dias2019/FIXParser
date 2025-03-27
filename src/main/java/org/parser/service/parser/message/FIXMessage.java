package org.parser.service.parser.message;

import org.parser.service.parser.tag_mapper.tag.FIXTag;

import java.util.HashMap;
import java.util.Map;

public class FIXMessage {

    private final Map<FIXTag, String> message;

    public FIXMessage() {
        this.message = new HashMap<>();
    }

    public void addTag(FIXTag tag, String value) {
        message.put(tag, value);
    }
}
