package centrikt.factorymonitoring.authserver.init;

import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.models.Setting;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.SettingRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@Slf4j
public class AdminInitializer implements CommandLineRunner {
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("AdminInitializer initialized");
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.debug("UserRepository set");
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        log.debug("PasswordEncoder set");
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail(username)) {
            User admin = new User();
            admin.setActive(true);
            admin.setEmail(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setMiddleName("Admin");
            admin.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
            admin.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
            Setting setting = new Setting();
            setting.setUser(admin);
            setting.setReportNotifications(List.of());
            setting.setTimezone("UTC+03:00");
            setting.setSubscribe(false);
            admin.setSetting(setting);
            userRepository.save(admin);
            log.info("Created admin: {}", username);
        } else {
            log.info("Admin already exists: {}", username);
        }
    }
}
