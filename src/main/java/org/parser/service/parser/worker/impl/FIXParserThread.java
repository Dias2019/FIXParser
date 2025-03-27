package org.parser.service.parser.worker.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parser.client.IMessageConsumer;
import org.parser.service.parser.FIXParser;
import org.parser.service.parser.message.FIXMessage;
import org.parser.service.parser.tag_mapper.IFIXTagTransformer;
import org.parser.service.parser.worker.IFIXParserThread;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;

public class FIXParserThread implements IFIXParserThread, Closeable {

    private static final Logger LOGGER = LogManager.getLogger(FIXParserThread.class);

    private final BlockingDeque<byte[]> queue;
    private final FIXParser parser;
    private final IMessageConsumer consumer;
    private volatile boolean stop = false;

    public FIXParserThread(final String name, final IMessageConsumer consumer, final IFIXTagTransformer tagMapper) {
        queue = new LinkedBlockingDeque<>(150);
        parser = new FIXParser(tagMapper);
        this.consumer = consumer;

        // Create and start a worker thread
        final Thread thread = new Thread(this::serviceQueueWrapper);
        thread.setName(name);
        thread.start();
    }

    @Override
    public boolean handleMessage(final byte[] message) {
        if (stop)
            return false;

        synchronized (queue) {
            queue.offer(message);
        }
        return true;
    }

    @Override
    public List<FIXMessage> getAllSuccessMessages() {
        return consumer.getAllMessages();
    }

    private void serviceQueueWrapper() {

        LOGGER.info("serviceQueue started");
        try {
            serviceQueue();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception exception) {
            LOGGER.error("Unexpected exception", exception);
        }
        LOGGER.info("serviceQueue stopped");
    }

    private void serviceQueue() throws InterruptedException {
        while (!stop) {
            byte[] msg = queue.take();
            // Skip empty messages (poison pills)
            if (msg.length == 0) {
                continue;
            }
            boolean success = parser.parse(msg, consumer);
            if (success)
                LOGGER.info("{}:  successfully parsed message", currentThread().getName());
        }
    }

    @Override
    public void close() throws IOException {
        stop = true;
        try {
            queue.offer(new byte[0], 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
