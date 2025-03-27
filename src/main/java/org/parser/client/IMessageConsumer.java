package org.parser.client;

import org.parser.service.parser.message.FIXMessage;

import java.util.List;
import java.util.function.Consumer;

public interface IMessageConsumer extends Consumer<FIXMessage> {
    List<FIXMessage> getAllMessages();
}