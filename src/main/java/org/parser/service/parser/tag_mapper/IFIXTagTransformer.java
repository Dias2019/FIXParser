package org.parser.service.parser.tag_mapper;

import org.parser.service.parser.tag_mapper.tag.FIXTag;

public interface IFIXTagTransformer {
    FIXTag getFixTagFromNumber(int number);
}
