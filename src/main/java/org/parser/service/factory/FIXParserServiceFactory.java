package org.parser.service.factory;

import org.parser.client.IMessageConsumer;
import org.parser.client.impl.StorageMessageConsumer;
import org.parser.service.FIXParserService;
import org.parser.service.parser.tag_mapper.IFIXTagTransformer;
import org.parser.service.parser.tag_mapper.impl.XmlFIXTagTransformer;
import org.parser.service.parser.worker.IFIXParserThread;
import org.parser.service.parser.worker.impl.FIXParserThread;

import java.util.Properties;

public class FIXParserServiceFactory {

    private static final String WORKER_COUNT_PROP = "fix.workerCount";
    private static final int DEFAULT_WORKER_COUNT = 4;


    public static FIXParserService create() throws Exception {
        Properties properties = PropertiesFactory.create();
        int workerCount = getWorkerCountFromProperties(properties);
        return createWithCustomConsumerAndWorkerCount(workerCount, new StorageMessageConsumer());
    }


    public static FIXParserService createWithCustomConsumerAndWorkerCount(int workers, IMessageConsumer consumer) throws Exception {

        IFIXTagTransformer tagTransformer = new XmlFIXTagTransformer();
        IFIXParserThread[] parsers = new IFIXParserThread[workers];
        for (int i = 0; i < workers; i++) {
            parsers[i] = new FIXParserThread("FIXParserThread-" + i, consumer, tagTransformer);
        }
        return new FIXParserService(parsers);
    }


    private static int getWorkerCountFromProperties(Properties properties) {

        int workerCount = DEFAULT_WORKER_COUNT;
        try {
            String workerCountStr = properties.getProperty(WORKER_COUNT_PROP);
            if (workerCountStr != null) {
                workerCount = Integer.parseInt(workerCountStr);
            }
        } catch (NumberFormatException e) {
            // Use default value if parsing fails
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return workerCount;
    }
}
