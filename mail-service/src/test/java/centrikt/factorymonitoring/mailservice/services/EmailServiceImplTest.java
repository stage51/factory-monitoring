package centrikt.factorymonitoring.mailservice.services;

import centrikt.factorymonitoring.mailservice.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.mailservice.services.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private EmailMessage emailMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private String username = "test@example.com";

    @Test
    void sendSimpleEmail_ShouldSendEmail() {
        // Arrange
        String[] toAddresses = {"recipient@example.com"};
        String subject = "Test Subject";
        String message = "Test Message";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(toAddresses);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        ReflectionTestUtils.setField(emailService, "username", username);
        // Act
        emailService.sendSimpleEmail(toAddresses, subject, message);

        // Assert
        verify(emailSender, times(1)).send(eq(simpleMailMessage));
    }

    @Test
    void receiveEmailMessage_ShouldSendEmail_WhenValidMessage() {
        // Arrange
        String[] toAddresses = {"recipient@example.com"};
        String subject = "Test Subject";
        String message = "Test Message";

        when(emailMessage.getToAddresses()).thenReturn(toAddresses);
        when(emailMessage.getSubject()).thenReturn(subject);
        when(emailMessage.getMessage()).thenReturn(message);

        // Act
        emailService.receiveEmailMessage(emailMessage);

        // Assert
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void receiveEmailMessage_ShouldNotSendEmail_WhenNoRecipients() {
        // Arrange
        when(emailMessage.getToAddresses()).thenReturn(new String[]{});
        when(emailMessage.getMessage()).thenReturn("Test Message");

        // Act
        emailService.receiveEmailMessage(emailMessage);

        // Assert
        verify(emailSender, never()).send(any(SimpleMailMessage.class));
    }
}

