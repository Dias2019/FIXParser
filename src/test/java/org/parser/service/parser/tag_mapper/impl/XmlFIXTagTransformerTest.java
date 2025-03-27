package org.parser.service.parser.tag_mapper.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parser.service.parser.tag_mapper.tag.FIXTag;
import org.parser.service.parser.tag_mapper.tag.FIXTagEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlFIXTagTransformerTest {

    private XmlFIXTagTransformer numberToFIXTagMapper;

    @BeforeEach
    public void setUp() throws Exception {
        numberToFIXTagMapper = new XmlFIXTagTransformer("/config/Test_FIX44.xml");
    }

    @Test
    public void testReadFieldsDefinition() {

        assertEquals(3, numberToFIXTagMapper.size(), "There should be 3 fields defined in our test XML file");

        FIXTag msgTypeTag = numberToFIXTagMapper.getFixTagFromNumber(35);
        assertEquals("MsgType", msgTypeTag.getName(), "Field 35 should be MsgType");
        assertEquals("STRING", msgTypeTag.getType(), "Field 35 should be of type STRING");

        assertTrue(msgTypeTag.hasEnumOptions(), "Field 35 should have enumOptions");
        assertEquals(2, msgTypeTag.enumsSize(), "MsgType should have 2 enum options");
        assertTrue(msgTypeTag.hasEnum("A") && msgTypeTag.getEnum("A").equals(new FIXTagEnum("A", "Logon")),
                "MsgType should contain enum A for Logon");
        assertTrue(msgTypeTag.hasEnum("D") && msgTypeTag.getEnum("D").equals(new FIXTagEnum("D", "NewOrderSingle")),
                "MsgType should contain enum D for NewOrderSingle");
    }

    @Test
    public void testReadHeaderMembers() {

        assertTrue(numberToFIXTagMapper.hasHeaderField("BeginString"), "Header should contain BeginString");
        assertTrue(numberToFIXTagMapper.hasHeaderField("BodyLength"), "Header should contain BodyLength");
    }

    @Test
    public void testReadTrailerMembers() {

        assertTrue(numberToFIXTagMapper.hasTrailerField("CheckSum"), "Trailer should contain CheckSum");
    }
}