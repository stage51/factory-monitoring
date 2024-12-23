package centrikt.factorymonitoring.mailservice.services;

import centrikt.factorymonitoring.mailservice.dtos.messages.EmailMessage;
import jakarta.mail.MessagingException;

import java.io.FileNotFoundException;

public interface EmailService {
    void sendSimpleEmail(String[] toAddresses, String subject, String message);
    void receiveEmailMessage(EmailMessage emailMessage);
}
