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
        log.debug("EmailServiceImpl initialized with JavaMailSender: {}", emailSender.getClass().getName());
    }

    @Override
    public void sendSimpleEmail(String[] toAddresses, String subject, String message) {
        log.info("Preparing to send email to: {}", (Object) toAddresses);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(toAddresses);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        try {
            log.debug("Sending email with subject: {}", subject);
            emailSender.send(simpleMailMessage);
            log.info("Email sent successfully to: {}", (Object) toAddresses);
        } catch (Exception e) {
            log.error("Error occurred while sending email to: {}", (Object) toAddresses, e);
        }
    }

    @RabbitListener(queues = "emailQueue")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        if (emailMessage.getToAddresses().length == 0) {
            log.warn("Email message with text ({}) has no recipients. Skipping...", emailMessage.getMessage());
            return;
        }

        log.info("Received email message from queue. Preparing to send email to: {}", (Object) emailMessage.getToAddresses());
        log.debug("Email subject: {}", emailMessage.getSubject());
        log.debug("Email message content: {}", emailMessage.getMessage());

        try {
            sendSimpleEmail(emailMessage.getToAddresses(), emailMessage.getSubject(), emailMessage.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while processing email message from queue", e);
        }
    }
}

