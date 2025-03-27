package org.parser.service;

import org.parser.service.parser.worker.IFIXParserThread;
import java.io.Closeable;
import java.io.IOException;

public class FIXParserService implements Closeable {

    private final IFIXParserThread[] parsers;
    private int messageCount;

    public FIXParserService(IFIXParserThread[] parserThreads) {
        parsers = parserThreads;
    }

    public boolean handleFixMessage(final byte[] message) {
        messageCount++;
        return parsers[messageCount % parsers.length].handleMessage(message);       //round-robin
    }

    public IFIXParserThread[] getParsers() {
        return parsers;
    }

    public int getMessageCount() {
        return messageCount;
    }

    @Override
    public void close() {
        for (final IFIXParserThread thread : this.getParsers()) {
            if (thread instanceof Closeable) {
                try {
                    ((Closeable) thread).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
