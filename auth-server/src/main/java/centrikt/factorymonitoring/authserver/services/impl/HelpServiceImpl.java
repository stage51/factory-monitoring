package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import centrikt.factorymonitoring.authserver.exceptions.InvalidConstraintException;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.HelpService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RefreshScope
public class HelpServiceImpl implements HelpService {
    @Value("${email.help-notification}")
    private Boolean helpNotification;

    @Value("${email.help-notification-for}")
    private String helpNotificationFor;

    private RabbitTemplate rabbitTemplate;
    private UserRepository userRepository;

    public HelpServiceImpl(RabbitTemplate rabbitTemplate, UserRepository userRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void sendHelpRequest(HelpRequest helpRequest) {
        if (helpNotification) {
            List<String> emails = new ArrayList<>();
            if (helpNotificationFor.equals("admin-manager")) {
                List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                emails.addAll(managersEmails);
                emails.addAll(adminsEmails);
            } else if (helpNotificationFor.equals("admin-only")) {
                List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                emails.addAll(adminsEmails);
            } else if (helpNotificationFor.equals("manager-only")) {
                List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                emails.addAll(managersEmails);
            } else throw new InvalidConstraintException("Invalid help notification for constraint " + helpNotificationFor);
            EmailMessage emailMessage = new EmailMessage(
                    emails.toArray(String[]::new),
                    "Factory Monitoring",
                    "Новое заявление в сервис\n" +
                            "от: " + helpRequest.getEmail() + "\n" +
                            "сообщение: " + helpRequest.getMessage()
            );
            rabbitTemplate.convertAndSend("emailQueue", emailMessage);
        }
    }

    private List<User> getManagers() {
        return userRepository.findAllByRole(Role.ROLE_MANAGER);
    }

    private List<User> getAdmins() {
        return userRepository.findAllByRole(Role.ROLE_ADMIN);
    }
}
