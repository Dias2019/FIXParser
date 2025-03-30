package org.parser;

import org.parser.service.FIXParserService;
import org.parser.service.factory.FIXParserServiceFactory;

public class FIXParserServiceAPI {

    public static void main(String[] args) {

        final String exampleMsg = "8=FIX.4.4\0019=65\00135=A\00134=5\00149=BANZAI\00152=20231123-17:20:39.148\00156=EXEC\00198=0\001108=30\00110=224\001";

        try (FIXParserService service = FIXParserServiceFactory.create()) {
            boolean success = service.handleFixMessage(exampleMsg.getBytes());
            assert success;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }
}