package org.parser.service.parser.result;

public class FIXParserResult {

    public final static FIXParserResult SUCCESS = new FIXParserResult("SUCCESS");
    public final static FIXParserResult FAILED = new FIXParserResult("FAILED");

    private final String name;
    private FIXParserResult(String name) {
        this.name = name;
    }
}
