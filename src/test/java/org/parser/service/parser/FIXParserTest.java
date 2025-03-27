package org.parser.service.parser;

import org.junit.jupiter.api.*;
import org.parser.client.IMessageConsumer;
import org.parser.client.impl.StorageMessageConsumer;
import org.parser.service.parser.tag_mapper.IFIXTagTransformer;
import org.parser.service.parser.tag_mapper.impl.XmlFIXTagTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FIXParserTest {

    private FIXParser parser;
    private IMessageConsumer consumer;

    @BeforeEach
    public void setUp() throws Exception {
        IFIXTagTransformer tagTransformer = new XmlFIXTagTransformer();
        parser = new FIXParser(tagTransformer);
        consumer = new StorageMessageConsumer();
    }

    @AfterEach
    public void cleanup() {
    }

    @Test
    public void testSimpleFIXMessage() {
        String inputMsg = "8=FIX.4.4\0019=65\00135=A\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=224\001";
        parser.parse(inputMsg.getBytes(), consumer);

        assertEquals(1, consumer.getAllMessages().size(), "One message should be consumed");
    }

    @Test
    public void testCorruptedFIXMessage() {
        String inputMsg = "*)(^^%(8=FIX.4.4\0019=65\00135=A\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=224\001";
        parser.parse(inputMsg.getBytes(), consumer);

        assertEquals(1, consumer.getAllMessages().size(), "One message should be consumed");
    }

    @Test
    public void testInvalidChecksumFIXMessage() {
        String inputMsg = "*)(^^%(8=FIX.4.4\0019=65\00135=A\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=220\001";
        parser.parse(inputMsg.getBytes(), consumer);

        assertEquals(0, consumer.getAllMessages().size(), "One message should be consumed");
    }

    @Test
    public void testInvalidTagFIXMessage() {
        String inputMsg = "8=FIX.4.4\0019=67\00135=A\0011000=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=060\001";
        parser.parse(inputMsg.getBytes(), consumer);

        assertEquals(0, consumer.getAllMessages().size(), "One message should be consumed");
    }

    @Test
    public void testInvalidValueFIXMessage() {
        String inputMsg = "8=FIX.4.4\0019=66\00135=XX\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=080\001";
        parser.parse(inputMsg.getBytes(), consumer);

        assertEquals(0, consumer.getAllMessages().size(), "One message should be consumed");
    }
}