package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.Setting;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.repos.OrganizationRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.impl.OrganizationServiceImpl;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.filter.FilterUtil;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FilterUtil<Organization> filterUtil;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void createOrganization_ValidRequest_ReturnsOrganizationResponse() {
        // Arrange
        String accessToken = "testToken";
        AuthOrganizationRequest request = new AuthOrganizationRequest("Test Organization", "testorg", "Завод", "Москва", "123456789012", "123456789", "ул. Пушкина, д. 100", "test@test.com", "89001009999");

        Organization organization = new Organization();
        organization.setShortName("Test Organization");
        organization.setTaxpayerNumber("123456789012");
        organization.setSpecialEmail("test@example.com");

        User user = new User();
        user.setEmail("user@example.com");
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
        organization.setUser(user);

        when(jwtTokenUtil.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        OrganizationResponse response = organizationService.createOrganization(accessToken, request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Organization", response.getShortName());
        assertEquals("123456789012", response.getTaxpayerNumber());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(organizationRepository, times(1)).save(any(Organization.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteOrganization_ValidAccessToken_UserAndOrganizationUpdated() {
        // Arrange
        String accessToken = "testToken";

        User user = new User();
        user.setEmail("test@example.com");

        Organization organization = new Organization();
        organization.setUser(user);
        user.setOrganization(organization);

        when(jwtTokenUtil.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        organizationService.deleteOrganization(accessToken);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(organizationRepository, times(1)).deleteByUser(user);
    }

    @Test
    void getPage_ValidParameters_ReturnsPageOfOrganizations() {
        // Arrange
        int size = 10;
        int number = 0;
        String sortBy = "shortName";
        String sortDirection = "ASC";
        Map<String, String> filters = Collections.emptyMap();
        Map<String, String> dateRanges = Collections.emptyMap();

        Organization organization = new Organization();
        organization.setShortName("Test Organization");

        User user = new User();
        user.setEmail("user@example.com");
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
        organization.setUser(user);

        Page<Organization> organizationPage = new PageImpl<>(Collections.singletonList(organization));

        when(filterUtil.buildSpecification(filters, dateRanges)).thenReturn((Specification<Organization>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("shortName"), organization.getShortName()));
        when(organizationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(organizationPage);

        // Act
        Page<OrganizationResponse> responsePage = organizationService.getPage(size, number, sortBy, sortDirection, filters, dateRanges);

        // Assert
        assertNotNull(responsePage);
        assertFalse(responsePage.isEmpty());
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test Organization", responsePage.getContent().get(0).getShortName());
        verify(organizationRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
