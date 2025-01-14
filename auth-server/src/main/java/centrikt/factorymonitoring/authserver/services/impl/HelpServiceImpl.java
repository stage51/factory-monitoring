package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import centrikt.factorymonitoring.authserver.exceptions.InvalidConstraintException;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.HelpService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("HelpServiceImpl initialized");
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.debug("RabbitTemplate set");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.debug("UserRepository set");
    }

    @Override
    public void sendHelpRequest(HelpRequest helpRequest) {
        log.trace("Entering sendHelpRequest method with helpRequest: {}", helpRequest);

        if (helpNotification) {
            List<String> emails = new ArrayList<>();
            log.debug("Help notification is enabled, helpNotificationFor: {}", helpNotificationFor);

            switch (helpNotificationFor) {
                case "admin-manager" -> {
                    log.info("Sending help request to admins and managers");
                    List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                    List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                    emails.addAll(managersEmails);
                    emails.addAll(adminsEmails);
                }
                case "admin-only" -> {
                    log.info("Sending help request to admins only");
                    List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                    emails.addAll(adminsEmails);
                }
                case "manager-only" -> {
                    log.info("Sending help request to managers only");
                    List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                    emails.addAll(managersEmails);
                }
                default -> {
                    log.error("Invalid help notification for constraint: {}", helpNotificationFor);
                    throw new InvalidConstraintException("Invalid help notification for constraint " + helpNotificationFor);
                }
            }

            EmailMessage emailMessage = new EmailMessage(
                    emails.toArray(String[]::new),
                    "Factory Monitoring",
                    "Новое заявление в сервис\n" +
                            "от: " + helpRequest.getEmail() + "\n" +
                            "сообщение: " + helpRequest.getMessage()
            );
            log.info("Sending email notification to recipients: {}", emails);
            rabbitTemplate.convertAndSend("emailQueue", emailMessage);
        } else {
            log.debug("Help notification is disabled, no email will be sent.");
        }

        log.trace("Exiting sendHelpRequest method");
    }

    private List<User> getManagers() {
        log.trace("Entering getManagers method");
        List<User> managers = userRepository.findAllByRole(Role.ROLE_MANAGER);
        log.debug("Found {} managers", managers.size());
        log.trace("Exiting getManagers method");
        return managers;
    }

    private List<User> getAdmins() {
        log.trace("Entering getAdmins method");
        List<User> admins = userRepository.findAllByRole(Role.ROLE_ADMIN);
        log.debug("Found {} admins", admins.size());
        log.trace("Exiting getAdmins method");
        return admins;
    }
}

