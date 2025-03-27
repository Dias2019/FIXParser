package org.parser.service.parser.tag_mapper.tag;

import java.util.Map;
import java.util.TreeMap;

public class FIXTag {

    private final int number;
    private final String name;
    private final String type;
    private final Map<String, FIXTagEnum> enums;

    public FIXTag(int number, String name, String type) {
        this.number = number;
        this.name = name;
        this.type = type;
        this.enums = new TreeMap<>();
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void addEnumOption(FIXTagEnum option) {
        enums.put(option.getValue(), option);
    }

    public boolean hasEnumOptions() {
        return !enums.isEmpty();
    }

    public boolean hasEnum(String option) {
        return enums.containsKey(option);
    }

    public FIXTagEnum getEnum(String option) {
        return enums.get(option);
    }

    public int enumsSize() {
        return enums.size();
    }

}
