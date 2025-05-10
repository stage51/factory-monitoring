package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import centrikt.factorymonitoring.authserver.exceptions.InvalidConstraintException;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.impl.HelpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HelpServiceImpl helpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendHelpRequest_shouldSendEmailsToAdminsAndManagers_whenHelpNotificationForIsAdminManager() {
        // Arrange
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setEmail("test@example.com");
        helpRequest.setMessage("Test message");

        User admin = new User();
        admin.setEmail("admin@example.com");
        User manager = new User();
        manager.setEmail("manager@example.com");

        ReflectionTestUtils.setField(helpService, "helpNotificationFor", "admin-manager");
        ReflectionTestUtils.setField(helpService, "helpNotification", true);
        when(userRepository.findAllByRole(Role.ROLE_ADMIN)).thenReturn(Arrays.asList(admin));
        when(userRepository.findAllByRole(Role.ROLE_MANAGER)).thenReturn(Arrays.asList(manager));

        // Act
        helpService.sendHelpRequest(helpRequest);

        // Assert
        EmailMessage expectedEmailMessage = new EmailMessage(
                new String[]{"manager@example.com", "admin@example.com"},
                "Factory Monitoring",
                "Новое заявление в сервис\nот: test@example.com\nсообщение: Test message"
        );

        verify(rabbitTemplate).convertAndSend("emailQueue", expectedEmailMessage);
    }

    @Test
    void sendHelpRequest_shouldSendEmailsToAdmins_whenHelpNotificationForIsAdminOnly() {
        // Arrange
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setEmail("test@example.com");
        helpRequest.setMessage("Test message");

        User admin = new User();
        admin.setEmail("admin@example.com");

        when(userRepository.findAllByRole(Role.ROLE_ADMIN)).thenReturn(Arrays.asList(admin));

        ReflectionTestUtils.setField(helpService, "helpNotificationFor", "admin-only");
        ReflectionTestUtils.setField(helpService, "helpNotification", true);

        // Act
        helpService.sendHelpRequest(helpRequest);

        // Assert
        EmailMessage expectedEmailMessage = new EmailMessage(
                new String[]{"admin@example.com"},
                "Factory Monitoring",
                "Новое заявление в сервис\nот: test@example.com\nсообщение: Test message"
        );

        verify(rabbitTemplate).convertAndSend("emailQueue", expectedEmailMessage);
    }

    @Test
    void sendHelpRequest_shouldSendEmailsToManagers_whenHelpNotificationForIsManagerOnly() {
        // Arrange
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setEmail("test@example.com");
        helpRequest.setMessage("Test message");

        User manager = new User();
        manager.setEmail("manager@example.com");

        when(userRepository.findAllByRole(Role.ROLE_MANAGER)).thenReturn(Arrays.asList(manager));

        ReflectionTestUtils.setField(helpService, "helpNotificationFor", "manager-only");
        ReflectionTestUtils.setField(helpService, "helpNotification", true);

        // Act
        helpService.sendHelpRequest(helpRequest);

        // Assert
        EmailMessage expectedEmailMessage = new EmailMessage(
                new String[]{"manager@example.com"},
                "Factory Monitoring",
                "Новое заявление в сервис\nот: test@example.com\nсообщение: Test message"
        );

        verify(rabbitTemplate).convertAndSend("emailQueue", expectedEmailMessage);
    }

    @Test
    void sendHelpRequest_shouldThrowException_whenHelpNotificationForIsInvalid() {
        // Arrange
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setEmail("test@example.com");
        helpRequest.setMessage("Test message");

        ReflectionTestUtils.setField(helpService, "helpNotificationFor", "invalid");
        ReflectionTestUtils.setField(helpService, "helpNotification", true);


        // Act
        // Assert
        try {
            helpService.sendHelpRequest(helpRequest);
        } catch (InvalidConstraintException e) {
            assertEquals("Invalid help notification for constraint invalid", e.getMessage());
        }
    }

    @Test
    void sendHelpRequest_shouldNotSendEmail_whenHelpNotificationIsFalse() {
        // Arrange
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setEmail("test@example.com");
        helpRequest.setMessage("Test message");

        ReflectionTestUtils.setField(helpService, "helpNotification", false);

        // Act
        helpService.sendHelpRequest(helpRequest);

        // Assert
        verify(rabbitTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }
}
