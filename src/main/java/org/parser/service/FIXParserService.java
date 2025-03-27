package org.parser.service;

import org.parser.service.factory.FIXParserServiceFactory;
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
        return parsers[messageCount % parsers.length].handleMessage(message);
    }

    public IFIXParserThread[] getParsers() {
        return parsers;
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

    public static void main(String[] args) {
        FIXParserService service = null;
        try {
            service = FIXParserServiceFactory.create();

            // Your application logic here
            // ...

        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            // Close all parser threads
            if (service != null) {
                service.close();
            }
        }
    }
}
