package org.parser.client.impl;

import org.parser.client.IMessageConsumer;
import org.parser.service.parser.message.FIXMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LatchCountDownMessageConsumer implements IMessageConsumer {

    private final List<FIXMessage> messages;
    private final CountDownLatch latch;

    public LatchCountDownMessageConsumer(CountDownLatch latch) {
        messages = new ArrayList<>();
        this.latch = latch;
    }

    @Override
    public List<FIXMessage> getAllMessages() {
        return messages;
    }

    @Override
    public void accept(FIXMessage fixMessage) {
        messages.add(fixMessage);
        latch.countDown();
    }
}
