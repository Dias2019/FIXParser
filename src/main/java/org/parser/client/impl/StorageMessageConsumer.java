package org.parser.client.impl;

import org.parser.client.IMessageConsumer;
import org.parser.service.parser.message.FIXMessage;

import java.util.ArrayList;
import java.util.List;

public class StorageMessageConsumer implements IMessageConsumer {

    private final List<FIXMessage> messages;

    public StorageMessageConsumer() {
        messages = new ArrayList<>();
    }

    @Override
    public synchronized void accept(final FIXMessage fixMessage) {
        messages.add(fixMessage);
    }

    @Override
    public List<FIXMessage> getAllMessages() {
        return messages;
    }
}
