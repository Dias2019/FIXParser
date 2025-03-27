package org.parser.service.parser.tag_mapper.tag;

import java.util.Objects;

public class FIXTagEnum {
    private final String value;
    private final String description;

    public FIXTagEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {return value;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FIXTagEnum that = (FIXTagEnum) o;
        return Objects.equals(value, that.value) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, description);
    }
}
