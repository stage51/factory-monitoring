package centrikt.factorymonitoring.mailservice.services.impl;

import centrikt.factorymonitoring.mailservice.services.EmailService;
import centrikt.factorymonitoring.mailservice.dtos.messages.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@Service
@RefreshScope
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String username;

    private JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleEmail(String[] toAddresses, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(toAddresses);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

    @RabbitListener(queues = "emailQueue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        if (emailMessage.getToAddresses().length == 0) {
            log.warn("Email message with text (" + emailMessage.getMessage() + ") has no recipients. Skipping...");
            return;
        }
        sendSimpleEmail(emailMessage.getToAddresses(), emailMessage.getSubject(), emailMessage.getMessage());
    }
}
