package centrikt.factorymonitoring.authserver.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;

import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.services.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;

@ExtendWith(MockitoExtension.class)
public class OrganizationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrganizationController organizationController;

    private OrganizationRequest organizationRequest;
    private OrganizationResponse organizationResponse;
    private AuthOrganizationRequest authOrganizationRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(organizationController).build();
        // Инициализация данных для тестов
        organizationRequest = new OrganizationRequest(1L, "org", "Test Organization", "Завод", "123456789012", "123456789", "ул. Пушкина", "Test Address", "test@test.com", "89001009933");
        authOrganizationRequest = new AuthOrganizationRequest("org", "Test Organization", "Завод", "123456789012", "123456789", "ул. Пушкина", "Test Address", "test@test.com", "89001009933");
        organizationResponse = OrganizationResponse.builder().id(1L).createdAt(ZonedDateTime.now()).updatedAt(ZonedDateTime.now().plusDays(1))
                .userId(1L).shortName("org").name("Test Organization").type("Завод").taxpayerNumber("123456789012")
                .reasonCode("123456789").address("ул. Пушкина").region("Москва").specialEmail("test@test.com").specialPhone("89001009933").build();
    }

    @Test
    void getOrganization_shouldReturnOrganization_whenValidId() throws Exception {
        // Мокируем сервис
        when(organizationService.get(1L)).thenReturn(organizationResponse);

        // Выполнение запроса
        mockMvc.perform(get("/api/v1/auth-server/organizations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andExpect(jsonPath("$.address").value("ул. Пушкина"));
    }

    @Test
    void createOrganization_shouldReturnCreatedOrganization() throws Exception {
        // Мокируем сервис
        when(organizationService.create(any(OrganizationRequest.class))).thenReturn(organizationResponse);

        // Выполнение запроса
        assertNotNull(authOrganizationRequest, "authOrganizationRequest is null");
        mockMvc.perform(post("/api/v1/auth-server/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andExpect(jsonPath("$.address").value("ул. Пушкина"));
    }

    @Test
    void updateOrganization_shouldReturnUpdatedOrganization() throws Exception {
        // Мокируем сервис
        when(organizationService.update(eq(1L), any(OrganizationRequest.class))).thenReturn(organizationResponse);

        // Выполнение запроса
        mockMvc.perform(put("/api/v1/auth-server/organizations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andExpect(jsonPath("$.address").value("ул. Пушкина"));
    }

    @Test
    void deleteOrganization_shouldReturnNoContent_whenValidId() throws Exception {
        // Выполнение запроса
        mockMvc.perform(delete("/api/v1/auth-server/organizations/1"))
                .andExpect(status().isNoContent());

        // Проверка, что метод delete был вызван в сервисе
        verify(organizationService, times(1)).delete(1L);
    }

    @Test
    void createOrganizationProfile_shouldReturnCreatedOrganization() throws Exception {
        // Мокируем сервис
        when(organizationService.createOrganization(anyString(), any(AuthOrganizationRequest.class))).thenReturn(organizationResponse);

        // Выполнение запроса
        mockMvc.perform(post("/api/v1/auth-server/organizations/profile")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authOrganizationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andExpect(jsonPath("$.address").value("ул. Пушкина"));
    }

    @Test
    void updateOrganizationProfile_shouldReturnUpdatedOrganization() throws Exception {
        // Мокируем сервис
        when(organizationService.updateOrganization(anyString(), any(AuthOrganizationRequest.class))).thenReturn(organizationResponse);

        // Выполнение запроса
        mockMvc.perform(put("/api/v1/auth-server/organizations/profile")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authOrganizationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organization"))
                .andExpect(jsonPath("$.address").value("ул. Пушкина"));
    }

    @Test
    void deleteOrganizationProfile_shouldReturnNoContent() throws Exception {
        // Выполнение запроса
        mockMvc.perform(delete("/api/v1/auth-server/organizations/profile")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());

        // Проверка, что метод deleteOrganization был вызван в сервисе
        verify(organizationService, times(1)).deleteOrganization(anyString());
    }
}
