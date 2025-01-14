package centrikt.factorymonitoring.modereport.exceptions;

import org.springframework.amqp.AmqpException;

public class MessageSendingException extends AmqpException {
    public MessageSendingException(String message) {
        super(message);
    }
}
