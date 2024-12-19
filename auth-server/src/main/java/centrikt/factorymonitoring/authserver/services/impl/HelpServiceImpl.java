package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RefreshScope
public class HelpServiceImpl implements HelpService {
    @Value("${email.help-notification}")
    private Boolean helpNotification;

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
            List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
            List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
            List<String> emails = new ArrayList<>(managersEmails);
            emails.addAll(adminsEmails);
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
