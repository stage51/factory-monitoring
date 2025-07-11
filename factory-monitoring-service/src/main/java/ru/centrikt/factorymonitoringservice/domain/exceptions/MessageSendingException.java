package ru.centrikt.factorymonitoringservice.domain.exceptions;

import org.springframework.amqp.AmqpException;

public class MessageSendingException extends AmqpException {
    public MessageSendingException(String message) {
        super(message);
    }
}
