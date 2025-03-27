package org.parser.service.parser.worker;

import org.parser.service.parser.message.FIXMessage;

import java.util.List;

public interface IFIXParserThread {
    boolean handleMessage(byte[] msg);
    List<FIXMessage> getAllSuccessMessages();
}
