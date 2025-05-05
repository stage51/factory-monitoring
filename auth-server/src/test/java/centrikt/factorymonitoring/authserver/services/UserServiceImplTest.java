package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.UploadAvatarResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.Setting;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.impl.UserServiceImpl;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.imageuploader.ImageUploader;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FilterUtil<User> filterUtil;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ImageUploader imageUploader;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder, filterUtil, entityValidator, jwtTokenUtil, rabbitTemplate, imageUploader);
    }

    @Test
    void create_UserSuccessfullyCreated_ReturnsUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest("John", "Doe", "john.doe@example.com", "password", "ROLE_USER");
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setRole(Role.ROLE_USER);
        user.setActive(true);
        user.setPassword("encodedPassword");
        Setting setting = new Setting();
        setting.setSubscribe(true);
        setting.setUser(user);
        setting.setTimezone("UTC+03:00");
        setting.setAvatarUrl("image.com");
        setting.setReportNotifications(List.of(ReportNotification.FIVE_MINUTE, ReportNotification.DAILY));
        user.setSetting(setting);

        ReflectionTestUtils.setField(userService, "registrationNotificationFor", "admin-only");
        ReflectionTestUtils.setField(userService, "registrationNotification", true);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = userService.create(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(userRequest.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void get_UserExists_ReturnsUserResponse() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("john.doe@example.com");
        user.setRole(Role.ROLE_USER);
        user.setActive(true);
        Setting setting = new Setting();
        setting.setSubscribe(true);
        setting.setUser(user);
        setting.setTimezone("UTC+03:00");
        setting.setAvatarUrl("image.com");
        setting.setReportNotifications(List.of(ReportNotification.FIVE_MINUTE, ReportNotification.DAILY));
        user.setSetting(setting);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.get(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void delete_UserExists_DeletesUser() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAll_UsersExist_ReturnsUserList() {
        // Arrange
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setRole(Role.ROLE_USER);
        user.setActive(true);
        Setting setting = new Setting();
        setting.setSubscribe(true);
        setting.setUser(user);
        setting.setTimezone("UTC+03:00");
        setting.setAvatarUrl("image.com");
        setting.setReportNotifications(List.of(ReportNotification.FIVE_MINUTE, ReportNotification.DAILY));
        user.setSetting(setting);

        when(userRepository.findAll()).thenReturn(List.of(user));

        // Act
        List<UserResponse> users = userService.getAll();

        // Assert
        assertNotNull(users);
        assertFalse(users.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void uploadAvatar_AvatarUploadEnabled_ReturnsAvatarResponse() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(imageUploader.saveFile(file)).thenReturn("avatarUrl");

        // Act
        ReflectionTestUtils.setField(userService, "avatarUpload", true);
        UploadAvatarResponse response = userService.uploadAvatar(file);

        // Assert
        assertNotNull(response);
        assertEquals("avatarUrl", response.getAvatarUrl());
        verify(imageUploader, times(1)).saveFile(file);
    }

    @Test
    void getPage_ValidParameters_ReturnsPageOfUsers() {
        // Arrange
        User user = new User();
        user.setId(1L); // Ensure the user has an ID
        user.setEmail("john.doe@example.com");
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        Setting setting = new Setting();
        setting.setSubscribe(true);
        setting.setUser(user);
        setting.setTimezone("UTC+03:00");
        setting.setAvatarUrl("image.com");
        setting.setReportNotifications(List.of(ReportNotification.FIVE_MINUTE, ReportNotification.DAILY));
        user.setSetting(setting);

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(filterUtil.buildSpecification(any(Map.class), any(Map.class))).thenReturn((Specification<User>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), user.getEmail()));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        // Act
        Page<UserResponse> page = userService.getPage(
                10,
                0,
                "firstName",
                "ASC",
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Assert
        assertNotNull(page);
        assertFalse(page.isEmpty());

        UserResponse userResponse = page.getContent().get(0);
        assertNotNull(userResponse);
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getRole().name(), userResponse.getRole());
        assertTrue(userResponse.isActive());

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

}

