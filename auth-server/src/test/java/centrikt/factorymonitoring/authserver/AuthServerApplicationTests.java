package centrikt.factorymonitoring.authserver;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.requests.*;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.config.location=classpath:/application-test.yml"
})
class AuthServerApplicationTests {

    @LocalServerPort
    private int port;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11-alpine")
            .withDatabaseName("user")
            .withUsername("admin")
            .withPassword("admin");

    @Container
    public static GenericContainer rabbitMQContainer = new GenericContainer<>(DockerImageName.parse("rabbitmq:management"))
            .withExposedPorts(5672, 15672)
            .withEnv("RABBITMQ_DEFAULT_USER", "admin")
            .withEnv("RABBITMQ_DEFAULT_PASS", "admin");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @DynamicPropertySource
    static void rabbitMqProperties(DynamicPropertyRegistry registry) {
        Integer amqpPort = rabbitMQContainer.getMappedPort(5672);
        Integer managementPort = rabbitMQContainer.getMappedPort(15672);

        registry.add("spring.rabbitmq.host", () -> "localhost");
        registry.add("spring.rabbitmq.port", () -> amqpPort);
        registry.add("spring.rabbitmq." +
                "username", () -> "admin");
        registry.add("spring.rabbitmq.password", () -> "admin");
        registry.add("spring.rabbitmq.virtual-host", () -> "/");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String getValidJwtToken() {
        return jwtTokenUtil.generateTokenForTest("admin@admin.com", "ROLE_ADMIN");
    }

    private String getValidJwtToken(String username) {
        return jwtTokenUtil.generateTokenForTest(username, "ROLE_ADMIN");
    }

    private HttpHeaders createAuthorizationHeaders() {
        String jwtToken = getValidJwtToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        return headers;
    }

    private HttpHeaders createAuthorizationHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        return headers;
    }

    private HttpHeaders createAuthorizationHeadersByUsername(String username) {
        String jwtToken = getValidJwtToken(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        return headers;
    }

    private HttpHeaders createAuthorizationHeadersByUsernameWithoutContentType(String username) {
        String jwtToken = getValidJwtToken(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
        rabbitMQContainer.stop();
    }

    @Test
    void contextLoads() {
        Assertions.assertTrue(postgreSQLContainer.isRunning(), "Postgres container is not running");
        Assertions.assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ container is not running");
        Assertions.assertNotNull(rabbitMQContainer.getFirstMappedPort(), "RabbitMQ port is not mapped");
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AuthControllerTests {

        private AccessRefreshTokenResponse accessRefreshTokenResponse;

        private String email;

        @BeforeAll
        void setUp() throws InterruptedException{
            this.accessRefreshTokenResponse = getAccessRefreshToken();
            this.email = "admin@admin.com";
            Thread.sleep(1000);
        }

        private AccessRefreshTokenResponse getAccessRefreshToken() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("admin@admin.com");
            loginRequest.setPassword("adminadmin");

            ResponseEntity<AccessRefreshTokenResponse> response = restTemplate.postForEntity(
                    "/api/v1/auth-server/auth/login", loginRequest, AccessRefreshTokenResponse.class);
            return response.getBody();
        }

        @Test
        void testLogin() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("admin@admin.com");
            loginRequest.setPassword("adminadmin");

            ResponseEntity<AccessRefreshTokenResponse> response = restTemplate.postForEntity(
                    "/api/v1/auth-server/auth/login", loginRequest, AccessRefreshTokenResponse.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        @Test
        void testRegister() {
            UserRequest userRequest = new UserRequest();
            userRequest.setEmail("newuser@example.com");
            userRequest.setPassword("password");
            userRequest.setFirstName("John");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("Middle");

            ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                    "/api/v1/auth-server/auth/register", userRequest, UserResponse.class);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        @Test
        void testRefreshToken() {
            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
            refreshTokenRequest.setRefreshToken(accessRefreshTokenResponse.getRefreshToken());

            ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                    "/api/v1/auth-server/auth/refresh-token", refreshTokenRequest, AccessTokenResponse.class);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        @Test
        void testLogout() {
            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
            refreshTokenRequest.setRefreshToken(accessRefreshTokenResponse.getRefreshToken());

            HttpEntity<RefreshTokenRequest> request = new HttpEntity<>(refreshTokenRequest, createAuthorizationHeaders());
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/auth/logout",
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void testCheckAuth() {
            HttpHeaders headers = createAuthorizationHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/auth/check",
                    HttpMethod.GET,
                    request,
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testCreateApiToken() {
            HttpHeaders headers = createAuthorizationHeaders(accessRefreshTokenResponse.getAccessToken());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<ApiTokenResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/auth/create-api-token?expiration=3600",
                    HttpMethod.GET,
                    request,
                    ApiTokenResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        @Test
        void testRecoveryPassword() {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/auth/forgot?email=" + email,
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthorizationHeaders()),
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }


    @Nested
    class HelpControllerTests {
        @Test
        void testSendHelpMessage() {
            HelpRequest helpRequest = new HelpRequest();
            helpRequest.setMessage("Need assistance with the system");

            HttpHeaders headers = createAuthorizationHeaders();
            HttpEntity<HelpRequest> request = new HttpEntity<>(helpRequest, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/help",
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    class OrganizationControllerTests {

        private HttpHeaders headers;

        @BeforeEach
        void setup() {
            headers = createAuthorizationHeaders();
        }

        @Test
        void testCrudOperations(){
            testCreateOrganization();
            testGetOrganization();
            testGetPage();
            testUpdateOrganization();
            testDeleteOrganization();
        }

        private void testGetOrganization() {
            ResponseEntity<OrganizationResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/1",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    OrganizationResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testCreateOrganization() {
            OrganizationRequest request = new OrganizationRequest(
                    1L,
                    "Short Name",
                    "Full Organization Name",
                    "Private",
                    "Region-1",
                    "1234567890",
                    "0987654321",
                    "123 Main St, City, Country",
                    "org@example.com",
                    "+1234567890"
            );

            HttpEntity<OrganizationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OrganizationResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations",
                    HttpMethod.POST,
                    entity,
                    OrganizationResponse.class
            );

            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testUpdateOrganization() {
            OrganizationRequest request = new OrganizationRequest(
                    1L,
                    "Updated Short Name",
                    "Updated Organization Name",
                    "Public",
                    "Region-2",
                    "9876543210",
                    "0123456789",
                    "456 Updated St, City, Country",
                    "updated@example.com",
                    "+0987654321"
            );

            HttpEntity<OrganizationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OrganizationResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/1",
                    HttpMethod.PUT,
                    entity,
                    OrganizationResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testDeleteOrganization() {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/1",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        private void testGetPage() {
            PageRequestDTO pageRequest = new PageRequestDTO();
            pageRequest.setSize(10);
            pageRequest.setNumber(0);

            HttpEntity<PageRequestDTO> entity = new HttpEntity<>(pageRequest, headers);

            ResponseEntity<TestPage<OrganizationResponse>> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/fetch",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<TestPage<OrganizationResponse>>() {}
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        @Test
        void testProfileOperations(){
            testCreateOrganizationProfile();
            testUpdateOrganizationProfile();
            testDeleteOrganizationProfile();
        }

        private void testCreateOrganizationProfile() {
            AuthOrganizationRequest request = new AuthOrganizationRequest(
                    "Short Profile Name",
                    "Full Profile Name",
                    "Non-Profit",
                    "Region-3",
                    "1122334455",
                    "5544332211",
                    "789 Profile St, City, Country",
                    "profile@example.com",
                    "+1122334455"
            );

            HttpEntity<AuthOrganizationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OrganizationResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/profile",
                    HttpMethod.POST,
                    entity,
                    OrganizationResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testUpdateOrganizationProfile() {
            AuthOrganizationRequest request = new AuthOrganizationRequest(
                    "Updated Short Profile Name",
                    "Updated Full Profile Name",
                    "Government",
                    "Region-4",
                    "6677889900",
                    "0099887766",
                    "101 Profile Updated St, City, Country",
                    "updated-profile@example.com",
                    "+6677889900"
            );

            HttpEntity<AuthOrganizationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OrganizationResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/profile",
                    HttpMethod.PUT,
                    entity,
                    OrganizationResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testDeleteOrganizationProfile() {
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/organizations/profile",
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );

            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }



    @Nested
    class OnlineControllerTests {
        @Test
        void testGetOnlineUsers() {
            PageRequestDTO pageRequestDTO = new PageRequestDTO();
            pageRequestDTO.setSize(10);
            pageRequestDTO.setNumber(0);

            HttpHeaders headers = createAuthorizationHeaders();
            HttpEntity<PageRequestDTO> request = new HttpEntity<>(pageRequestDTO, headers);

            ResponseEntity<TestPage<OnlineResponse>> response = restTemplate.exchange(
                    "/api/v1/auth-server/onlines/fetch",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<TestPage<OnlineResponse>>() {}
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }
    }

    @Nested
    class RefreshTokenControllerTests {

        @Test
        void testGetPage() {
            PageRequestDTO pageRequestDTO = new PageRequestDTO();
            pageRequestDTO.setSize(10);
            pageRequestDTO.setNumber(0);

            HttpHeaders headers = createAuthorizationHeaders();
            HttpEntity<PageRequestDTO> request = new HttpEntity<>(pageRequestDTO, headers);

            ResponseEntity<TestPage<RefreshTokenResponse>> response = restTemplate.exchange(
                    "/api/v1/auth-server/refresh-tokens/fetch",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<TestPage<RefreshTokenResponse>>() {}
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }
    }

    @Nested
    class RoleControllerTests {

        @Test
        void testGetRoles() {
            HttpHeaders headers = createAuthorizationHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<TestPage<RoleResponse>> response = restTemplate.exchange(
                    "/api/v1/auth-server/roles",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<TestPage<RoleResponse>>() {}
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertFalse(response.getBody().getContent().isEmpty());
        }
    }

    @Nested
    class UserControllerTests {

        @Test
        void testCrudOperations() {
            Long id = testCreateUser();
            testGetUser(id);
            testGetPage();
            testUpdateUser(id);
            testDeleteUser(id);
        }

        private void testGetUser(Long userId) {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/{id}",
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthorizationHeaders()),
                    UserResponse.class,
                    userId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private Long testCreateUser() {
            AdminUserRequest userRequest = new AdminUserRequest();
            userRequest.setEmail("newuser2@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("John");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("John");
            userRequest.setActive(true);
            userRequest.setRole("ROLE_USER");

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users",
                    HttpMethod.POST,
                    new HttpEntity<>(userRequest, createAuthorizationHeaders()),
                    UserResponse.class
            );

            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            return response.getBody().getId();
        }

        private void testUpdateUser(Long userId) {
            AdminUserRequest userRequest = new AdminUserRequest();
            userRequest.setEmail("newuser2@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("Mike");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("Mike");
            userRequest.setActive(true);
            userRequest.setRole("ROLE_USER");

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(userRequest, createAuthorizationHeaders()),
                    UserResponse.class,
                    userId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testDeleteUser(Long userId) {

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/{id}",
                    HttpMethod.DELETE,
                    new HttpEntity<>(createAuthorizationHeaders()),
                    Void.class,
                    userId
            );

            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        private void testGetPage() {
            PageRequestDTO pageRequestDTO = new PageRequestDTO();
            pageRequestDTO.setSize(10);
            pageRequestDTO.setNumber(0);

            ResponseEntity<TestPage<UserResponse>> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/fetch",
                    HttpMethod.POST,
                    new HttpEntity<>(pageRequestDTO, createAuthorizationHeaders()),
                    new ParameterizedTypeReference<TestPage<UserResponse>>() {}
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertTrue(response.getBody().getContent().size() > 0);
        }

        @Test
        void testApproveUser() {
            AdminUserRequest userRequest = new AdminUserRequest();
            userRequest.setEmail("newuser3@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("John");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("John");
            userRequest.setActive(true);
            userRequest.setRole("ROLE_GUEST");

            ResponseEntity<UserResponse> userResponse = restTemplate.exchange(
                    "/api/v1/auth-server/users",
                    HttpMethod.POST,
                    new HttpEntity<>(userRequest, createAuthorizationHeaders()),
                    UserResponse.class
            );

            Long userId = userResponse.getBody().getId();

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/{id}/approve",
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthorizationHeaders()),
                    Void.class,
                    userId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testDisapproveUser() {
            AdminUserRequest userRequest = new AdminUserRequest();
            userRequest.setEmail("newuser4@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("John");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("John");
            userRequest.setActive(true);
            userRequest.setRole("ROLE_GUEST");

            ResponseEntity<UserResponse> userResponse = restTemplate.exchange(
                    "/api/v1/auth-server/users",
                    HttpMethod.POST,
                    new HttpEntity<>(userRequest, createAuthorizationHeaders()),
                    UserResponse.class
            );

            Long userId = userResponse.getBody().getId();

            ResponseEntity<Void> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/{id}/disapprove",
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthorizationHeaders()),
                    Void.class,
                    userId
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testProfileOperations() throws IOException {
            AdminUserRequest userRequest = new AdminUserRequest();
            userRequest.setEmail("newuser5@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("John");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("John");
            userRequest.setActive(true);
            userRequest.setRole("ROLE_GUEST");

            ResponseEntity<UserResponse> userResponse = restTemplate.exchange(
                    "/api/v1/auth-server/users",
                    HttpMethod.POST,
                    new HttpEntity<>(userRequest, createAuthorizationHeadersByUsername("newuser5@example.com")),
                    UserResponse.class
            );

            testProfile();
            testUpdateProfile();
            testUpdateSettings();
            testUploadAvatar();
        }

        private void testProfile() {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/profile",
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthorizationHeadersByUsername("newuser5@example.com")),
                    UserResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testUpdateProfile() {
            UserRequest userRequest = new UserRequest();
            userRequest.setEmail("newuser5@example.com");
            userRequest.setPassword("password123");
            userRequest.setFirstName("Mike>");
            userRequest.setLastName("Doe");
            userRequest.setMiddleName("Mike");

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/profile",
                    HttpMethod.PUT,
                    new HttpEntity<>(userRequest, createAuthorizationHeadersByUsername("newuser5@example.com")),
                    UserResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testUpdateSettings() {
            SettingRequest settingRequest = new SettingRequest();
            settingRequest.setReportNotifications(List.of());
            settingRequest.setTimezone("UTC+00:00");
            settingRequest.setAvatarUrl("http://example.com/avatar.png");
            settingRequest.setSubscribe(false);

            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/profile/setting",
                    HttpMethod.PUT,
                    new HttpEntity<>(settingRequest, createAuthorizationHeadersByUsername("newuser5@example.com")),
                    UserResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
        }

        private void testUploadAvatar() throws IOException {
            byte[] imageContent = Files.readAllBytes(Paths.get("src/test/resources/test.png"));
            MultipartFile file = new MockMultipartFile("file", "test.png", "multipart/form-data", imageContent);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpHeaders headers = createAuthorizationHeadersByUsernameWithoutContentType("newuser5@example.com");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<UploadAvatarResponse> response = restTemplate.exchange(
                    "/api/v1/auth-server/users/profile/avatar",
                    HttpMethod.POST,
                    requestEntity,
                    UploadAvatarResponse.class
            );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

    }

    private static class TestPage<T> extends PageImpl<T> {
        @JsonIgnore
        private Pageable pageable;

        TestPage(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
            this.pageable = pageable;
        }

        public TestPage() {
            super(new ArrayList<>(), Pageable.unpaged(), 0);
        }
    }
}
