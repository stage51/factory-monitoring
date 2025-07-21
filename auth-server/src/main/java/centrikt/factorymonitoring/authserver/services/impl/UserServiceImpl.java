package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UploadAvatarResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.exceptions.InvalidConstantException;
import centrikt.factorymonitoring.authserver.exceptions.MethodDisabledException;
import centrikt.factorymonitoring.authserver.mappers.SettingMapper;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.exceptions.IllegalArgumentException;
import centrikt.factorymonitoring.authserver.mappers.UserMapper;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.UserService;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.imageuploader.ImageUploader;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RefreshScope
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${date-time.default-user-timezone}")
    private String defaultUserTimezone;

    @Value("${email.registration-notification}")
    private Boolean registrationNotification;

    @Value("${email.registration-notification-for}")
    private String registrationNotificationFor;

    @Value("${user.avatar-upload}")
    private Boolean avatarUpload;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private FilterUtil<User> filterUtil;
    private EntityValidator entityValidator;
    private JwtTokenUtil jwtTokenUtil;
    private RabbitTemplate rabbitTemplate;
    private ImageUploader imageUploader;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           FilterUtil<User> filterUtil , EntityValidator entityValidator,
                           JwtTokenUtil jwtTokenUtil, RabbitTemplate rabbitTemplate,
                           ImageUploader imageUploader) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.filterUtil = filterUtil;
        this.entityValidator = entityValidator;
        this.jwtTokenUtil = jwtTokenUtil;
        this.rabbitTemplate = rabbitTemplate;
        this.imageUploader = imageUploader;
        log.info("UserServiceImpl initialized");
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
    @Autowired
    public void setFilterUtil(FilterUtil<User> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }
    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        log.debug("JwtTokenUtil set");
    }
    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.debug("RabbitTemplate set");
    }
    @Autowired
    public void setImageUploader(ImageUploader imageUploader) {
        this.imageUploader = imageUploader;
        log.debug("ImageUploader set");
    }

    @Override
    public UserResponse create(UserRequest dto) {
        log.trace("Entering create method with user request: {}", dto);
        try {
            entityValidator.validate(dto);
            User user = UserMapper.toEntityFromCreateRequest(dto, defaultUserTimezone);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            List<String> emails = new ArrayList<>();
            if (registrationNotificationFor.equals("admin-manager")) {
                List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                emails.addAll(managersEmails);
                emails.addAll(adminsEmails);
            } else if (registrationNotificationFor.equals("admin-only")) {
                List<String> adminsEmails = getAdmins().stream().map(User::getEmail).toList();
                emails.addAll(adminsEmails);
            } else if (registrationNotificationFor.equals("manager-only")) {
                List<String> managersEmails = getManagers().stream().map(User::getEmail).toList();
                emails.addAll(managersEmails);
            } else {
                log.warn("Invalid registration notification for constraint: {}", registrationNotificationFor);
                throw new InvalidConstantException("Invalid registration notification for constraint " + registrationNotificationFor);
            }
            if (registrationNotification) {
                log.debug("Sending registration notification to: {}", emails);
                rabbitTemplate.convertAndSend("emailQueue",
                        new EmailMessage(
                                emails.toArray(String[]::new),
                                "Factory Monitoring",
                                String.format("Зарегистрировался новый пользователь: %s %s с почтой %s."
                                        , user.getFirstName(), user.getLastName(), user.getEmail())));
            }
            log.info("User created successfully with email: {}", user.getEmail());
            return UserMapper.toResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user with email: {}, user already exists", dto.getEmail(), e);
            throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exists");
        }
    }

    @Override
    public UserResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        return UserMapper.toResponse(userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id: {}", id);
            return new EntityNotFoundException("User not found with id: " + id);
        }));
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        log.trace("Entering update method with id: {}, user request: {}", id, dto);
        entityValidator.validate(dto);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id: {}", id);
            return new EntityNotFoundException("User not found with id: " + id);
        });
        return UserMapper.toResponse(userRepository.saveAndFlush(UserMapper.toEntityFromUpdateRequest(user, dto)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User with id: {} deleted successfully", id);
        } else {
            log.error("User not found with id: {}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public List<UserResponse> getAll() {
        log.trace("Entering getAll method");
        return userRepository.findAll().stream().map(UserMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                      Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, page number: {}, sortBy: {}, sortDirection: {}", size, number, sortBy, sortDirection);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "firstName");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<User> specification = filterUtil.buildSpecification(filters, dateRanges);
        return userRepository.findAll(specification, pageable).map(UserMapper::toResponse);
    }

    @Override
    public UserResponse getByEmail(String email) {
        log.trace("Entering getByEmail method with email: {}", email);
        return UserMapper.toResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email)));
    }

    @Override
    public UserResponse getProfile(String accessToken) {
        log.trace("Entering getProfile method with access token: {}", accessToken);
        String username = jwtTokenUtil.extractUsername(accessToken);
        return UserMapper.toResponse(
                userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username))
        );
    }

    @Override
    public UserResponse updateProfile(String accessToken, UserRequest userRequest) {
        log.trace("Entering updateProfile method with access token: {}, user request: {}", accessToken, userRequest);
        entityValidator.validate(userRequest);
        String username = jwtTokenUtil.extractUsername(accessToken);
        User existingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        existingUser = UserMapper.toEntityFromUpdateRequest(existingUser, userRequest);
        existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        return UserMapper.toResponse(userRepository.save(existingUser));
    }

    @Override
    public SettingResponse updateSetting(String accessToken, SettingRequest settingRequest) {
        log.trace("Entering updateSetting method with access token: {}, setting request: {}", accessToken, settingRequest);
        entityValidator.validate(settingRequest);
        String username = jwtTokenUtil.extractUsername(accessToken);
        User existingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        existingUser.setSetting(SettingMapper.toEntity(existingUser.getSetting(), settingRequest));
        userRepository.save(existingUser);
        return SettingMapper.toResponse(existingUser.getSetting());
    }

    @Override
    public UserResponse create(AdminUserRequest adminUserRequest) {
        log.trace("Entering create method with admin user request: {}", adminUserRequest);
        try {
            entityValidator.validate(adminUserRequest);
            User user = UserMapper.toEntityFromCreateRequest(adminUserRequest, defaultUserTimezone);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return UserMapper.toResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating admin user with email: {}, user already exists", adminUserRequest.getEmail(), e);
            throw new IllegalArgumentException("User with email " + adminUserRequest.getEmail() + " already exists");
        }
    }

    @Override
    public UserResponse update(Long id, AdminUserRequest adminUserRequest) {
        log.trace("Entering update method with id: {}, admin user request: {}", id, adminUserRequest);
        entityValidator.validate(adminUserRequest);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user = UserMapper.toEntityFromUpdateRequest(user, adminUserRequest);
        user.setPassword(passwordEncoder.encode(adminUserRequest.getPassword()));
        return UserMapper.toResponse(userRepository.saveAndFlush(user));
    }

    @Override
    public UploadAvatarResponse uploadAvatar(MultipartFile file) throws IOException {
        log.trace("Entering uploadAvatar method with file: {}", file.getOriginalFilename());
        if (avatarUpload) {
            String avatarUrl = imageUploader.saveFile(file);
            return UploadAvatarResponse.builder().avatarUrl(avatarUrl).build();
        } else {
            log.warn("Avatar upload is disabled");
            throw new MethodDisabledException("Upload avatar not enabled");
        }
    }

    @Override
    public void approve(Long id) {
        log.trace("Entering approve method with id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        log.info("User with id: {} approved successfully", id);
    }

    @Override
    public void disapprove(Long id) {
        log.trace("Entering disapprove method with id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
        log.info("User with id: {} disapproved successfully", id);
    }

    private List<User> getManagers() {
        log.trace("Entering getManagers method");
        return userRepository.findAllByRole(Role.ROLE_MANAGER);
    }

    private List<User> getAdmins() {
        log.trace("Entering getAdmins method");
        return userRepository.findAllByRole(Role.ROLE_ADMIN);
    }
}

