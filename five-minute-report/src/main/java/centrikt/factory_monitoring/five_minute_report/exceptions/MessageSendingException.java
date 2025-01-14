package centrikt.factory_monitoring.five_minute_report.exceptions;

import org.springframework.amqp.AmqpException;

public class MessageSendingException extends AmqpException {
    public MessageSendingException(String message) {
        super(message);
    }
}
