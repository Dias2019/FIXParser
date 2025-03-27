package org.parser.service.parser.tag_mapper.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parser.service.parser.tag_mapper.tag.FIXTag;
import org.parser.service.parser.tag_mapper.tag.FIXTagEnum;
import org.parser.service.parser.tag_mapper.IFIXTagTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;


public class XmlFIXTagTransformer implements IFIXTagTransformer {

    private static final Logger LOGGER = LogManager.getLogger(XmlFIXTagTransformer.class);

    private final Map<Integer, FIXTag> numberToTagMapper;

    private final Set<String> headerFields;
    private final Set<String> trailerFields;


    public XmlFIXTagTransformer() throws Exception {
        numberToTagMapper = new TreeMap<>();
        headerFields = new TreeSet<>();
        trailerFields = new TreeSet<>();

        read();
    }

    public XmlFIXTagTransformer(final String filePath) throws Exception {
        numberToTagMapper = new TreeMap<>();
        headerFields = new TreeSet<>();
        trailerFields = new TreeSet<>();

        readFileFromResource(filePath);
    }


    public void read() throws Exception {
        readFileFromResource("/config/FIX44.xml");
    }

    private void readFileFromResource(final String filePath) throws Exception {
        LOGGER.info("reading FIX resource: {} ", filePath);

        try (final InputStream in = getClass().getResourceAsStream(filePath)) {
            read(in);
        }
    }

    private void read(final InputStream in) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.parse(in);
        doc.normalizeDocument();

        readFieldsDefinition(doc);
        readHeaderMembers(doc);
        readTrailerMembers(doc);
    }

    private void readFieldsDefinition(final Document document) {

        Element fieldsElement = (Element) document.getElementsByTagName("fields").item(0);
        NodeList fieldsList = fieldsElement.getElementsByTagName("field");
        for (int i = 0; i < fieldsList.getLength(); i++) {
            Element fieldElement = (Element) fieldsList.item(i);
            int number = Integer.parseInt(fieldElement.getAttribute("number"));
            String name = fieldElement.getAttribute("name");
            String type = fieldElement.getAttribute("type");
            FIXTag tagInfo = new FIXTag(number, name, type);

            NodeList valueList = fieldElement.getElementsByTagName("value");
            for (int j = 0; j < valueList.getLength(); j++) {
                Element valueElement = (Element) valueList.item(j);
                String value = valueElement.getAttribute("enum");
                String description = valueElement.getAttribute("description");
                FIXTagEnum option = new FIXTagEnum(value, description);
                tagInfo.addEnumOption(option);
            }
            numberToTagMapper.put(tagInfo.getNumber(), tagInfo);
        }
    }

    private void readHeaderMembers(final Document document) {

        Element headerElement = (Element) document.getElementsByTagName("header").item(0);

        NodeList headerFieldList = headerElement.getElementsByTagName("field");
        for (int i = 0; i < headerFieldList.getLength(); i++) {
            Element fieldElement = (Element) headerFieldList.item(i);
            String fieldName = fieldElement.getAttribute("name");
            headerFields.add(fieldName);
        }

        NodeList groupList = headerElement.getElementsByTagName("group");
        for (int i = 0; i < groupList.getLength(); i++) {
            Element groupElement = (Element) groupList.item(i);
            String groupName = groupElement.getAttribute("name");
            headerFields.add(groupName);

            NodeList groupFieldList = groupElement.getElementsByTagName("field");
            for (int j = 0; j < groupFieldList.getLength(); j++) {
                Element groupFieldElement = (Element) groupFieldList.item(j);
                String groupFieldName = groupFieldElement.getAttribute("name");
                headerFields.add(groupFieldName);
            }
        }
    }

    private void readTrailerMembers(final Document document) {

        Element trailerElement = (Element) document.getElementsByTagName("trailer").item(0);

        NodeList trailerFieldList = trailerElement.getElementsByTagName("field");
        for (int i = 0; i < trailerFieldList.getLength(); i++) {
            Element fieldElement = (Element) trailerFieldList.item(i);
            String fieldName = fieldElement.getAttribute("name");
            trailerFields.add(fieldName);
        }
    }


    @Override
    public FIXTag getFixTagFromNumber(final int number) {
        return numberToTagMapper.get(number);
    }

    public int size() {
        return numberToTagMapper.size();
    }

    public boolean hasHeaderField(final String field) {
        return headerFields.contains(field);
    }

    public boolean hasTrailerField(final String field) {
        return trailerFields.contains(field);
    }
}
