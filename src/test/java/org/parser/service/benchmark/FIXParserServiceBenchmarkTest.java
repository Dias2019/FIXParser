package org.parser.service.benchmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parser.client.impl.LatchCountDownMessageConsumer;
import org.parser.service.FIXParserService;
import org.parser.service.factory.FIXParserServiceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FIXParserServiceBenchmarkTest {

    private List<byte[]> messages;
    private CountDownLatch completionLatch;

    @BeforeEach
    public void setup() throws Exception {
        messages = getSameFixMessages();
        completionLatch = new CountDownLatch(messages.size());
    }

    @Test
    public void benchmarkTestWithSingleThread() throws Exception {

        int worker = 1;
        FIXParserService service = createServiceWithLatchConsumersAndCustomWorkers(worker, completionLatch);

        // warming-up jvm
        for (int i = 0; i < 15; i++) {
            benchmarkRun(service, worker, new ArrayList<>());
        }

        List<Long> latencies = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            benchmarkRun(service, worker, latencies);
        }

        System.out.println("Maximum latency for Single Threaded: " + Collections.max(latencies) + "ns");
        OptionalDouble average = latencies.stream().mapToLong(Long::longValue).average();
        average.ifPresent(value -> System.out.println("Average latency for Single Threaded: " + (int)value + "ns"));
        System.out.println("Avg number of FIXMessages per second for Single Threaded: "
                + messages.size() / (average.getAsDouble() / 1_000_000));
        System.out.println();

        service.close();

    }

    @Test
    public void benchmarkTestWithMultiThread() throws Exception {

        int worker = Runtime.getRuntime().availableProcessors();;
        FIXParserService service = createServiceWithLatchConsumersAndCustomWorkers(worker, completionLatch);

        // warming-up jvm
        for (int i = 0; i < 15; i++) {
            benchmarkRun(service, worker, new ArrayList<>());
        }

        List<Long> latencies = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            benchmarkRun(service, worker, latencies);
        }

        System.out.println("Maximum latency for Multi Threaded: " + Collections.max(latencies) + "ns");
        OptionalDouble average = latencies.stream().mapToLong(Long::longValue).average();
        average.ifPresent(value -> System.out.println("Average latency for Multi Threaded: " + (int)value + "ns"));
        System.out.println("Avg number of FIXMessages per second for Multi Threaded: "
                + messages.size() / (average.getAsDouble() / 1_000_000));
        System.out.println();

        service.close();

    }

    @Test
    public void benchmarkTestWithTooMuchThread() throws Exception {

        int worker = 100;
        FIXParserService service = createServiceWithLatchConsumersAndCustomWorkers(worker, completionLatch);

        // warming-up jvm
        for (int i = 0; i < 15; i++) {
            benchmarkRun(service, worker, new ArrayList<>());
        }

        List<Long> latencies = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            benchmarkRun(service, worker, latencies);
        }

        System.out.println("Maximum latency for Too Much Threaded: " + Collections.max(latencies) + "ns");
        OptionalDouble average = latencies.stream().mapToLong(Long::longValue).average();
        average.ifPresent(value -> System.out.println("Average latency for Too Much Threaded: " + (int)value + "ns"));
        System.out.println("Avg number of FIXMessages per second for Too Much Threaded: "
                + messages.size() / (average.getAsDouble() / 1_000_000));
        System.out.println();

        service.close();

    }


    public void benchmarkRun(FIXParserService service, int worker, List<Long> latency) throws Exception {

        boolean allMsgParsedSuccessfully = true;
        long start = System.nanoTime();
        for (byte[] message : messages) {
            boolean result = service.handleFixMessage(message);
            allMsgParsedSuccessfully &= result;
        }
        // Wait for all messages to be processed (with timeout for safety)
        boolean allProcessed = completionLatch.await(30, TimeUnit.SECONDS);
        long end = System.nanoTime();

        if (!allProcessed) {
            System.out.println("WARNING: Timed out waiting for message processing!");
        }

        assertTrue(allMsgParsedSuccessfully);

        long duration = end - start;
        latency.add(duration);
    }

    private FIXParserService createServiceWithLatchConsumersAndCustomWorkers(int workers, CountDownLatch latch) throws Exception {
        return FIXParserServiceFactory.createWithCustomConsumerAndWorkerCount(workers, new LatchCountDownMessageConsumer(latch));
    }

    private static List<byte[]> getSameFixMessages() {
        String inputMsg = "8=FIX.4.4\0019=65\00135=A\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=224\001";
        List<byte[]> testData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            byte[] byteArray = inputMsg.getBytes();
            testData.add(byteArray);
        }
        return testData;
    }

    @Deprecated
    private static List<byte[]> getTestData() {
        List<byte[]> testData = new ArrayList<>();
        final String resourcePath = "benchmarkTest/fix_messages.txt";

        try (InputStream inputStream = FIXParserServiceBenchmarkTest.class.getClassLoader()
                .getResourceAsStream(resourcePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace('|', '\001');
                testData.add(line.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testData;
    }
}
