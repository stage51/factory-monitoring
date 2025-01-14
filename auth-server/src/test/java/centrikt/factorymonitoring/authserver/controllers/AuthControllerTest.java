package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.ApiTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_shouldReturnTokens_whenValidRequest() throws Exception {
        // Подготовка данных
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        AccessRefreshTokenResponse response = new AccessRefreshTokenResponse("accessToken", "refreshToken");

        // Мокируем поведение сервиса
        when(authService.createTokens(any(LoginRequest.class))).thenReturn(response);

        // Выполнение запроса
        mockMvc.perform(post("/api/v1/auth-server/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).createTokens(any(LoginRequest.class));
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken_whenValidRequest() throws Exception {
        // Подготовка данных
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refreshToken");
        AccessTokenResponse response = new AccessTokenResponse("newAccessToken");

        // Мокируем поведение сервиса
        when(authService.refreshAccessToken(any(RefreshTokenRequest.class))).thenReturn(response);

        // Выполнение запроса
        mockMvc.perform(post("/api/v1/auth-server/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refreshToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"));

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).refreshAccessToken(any(RefreshTokenRequest.class));
    }

    @Test
    void register_shouldReturnUserResponse_whenValidRequest() throws Exception {
        // Подготовка данных
        UserRequest userRequest = UserRequest.builder().email("test@example.com").password("password").firstName("USER").build();
        UserResponse userResponse = UserResponse.builder().email("test@example.com").firstName("USER").build();

        // Мокируем поведение сервиса
        when(userService.create(any(UserRequest.class))).thenReturn(userResponse);

        // Выполнение запроса
        mockMvc.perform(post("/api/v1/auth-server/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // Проверка взаимодействия с сервисом
        verify(userService, times(1)).create(any(UserRequest.class));
    }

    @Test
    void logout_shouldReturnNoContent_whenValidRequest() throws Exception {

        // Мокируем поведение сервиса
        doNothing().when(authService).revokeRefreshToken(any(RefreshTokenRequest.class));

        // Выполнение запроса
        mockMvc.perform(post("/api/v1/auth-server/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refreshToken\"}"))
                .andExpect(status().isNoContent());

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).revokeRefreshToken(any(RefreshTokenRequest.class));
    }


    @Test
    void checkAuth_shouldReturnOk_whenValidAuthorizationHeader() throws Exception {
        // Мокируем поведение сервиса
        when(authService.validateToken(anyString())).thenReturn(true);
        doNothing().when(authService).addOnline(anyString(), any());

        // Выполнение запроса
        mockMvc.perform(get("/api/v1/auth-server/auth/check")
                        .header("Authorization", "Bearer accessToken"))
                .andExpect(status().isOk());

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).validateToken(anyString());
        verify(authService, times(1)).addOnline(anyString(), any());
    }

    @Test
    void createApiToken_shouldReturnApiToken_whenValidRequest() throws Exception {
        // Подготовка данных
        String apiToken = "apiToken";
        long expiration = 3600L;

        // Мокируем поведение сервиса
        when(authService.createApiToken(anyString(), anyLong())).thenReturn(new ApiTokenResponse(apiToken));

        // Выполнение запроса
        mockMvc.perform(get("/api/v1/auth-server/auth/create-api-token")
                        .header("Authorization", "Bearer accessToken")
                        .param("expiration", String.valueOf(expiration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiToken").value("apiToken"));

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).createApiToken(anyString(), anyLong());
    }

    @Test
    void forgotPassword_shouldReturnOk_whenValidRequest() throws Exception {
        // Мокируем поведение сервиса
        doNothing().when(authService).forgotPassword(anyString());

        // Выполнение запроса
        mockMvc.perform(get("/api/v1/auth-server/auth/forgot")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk());

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).forgotPassword(anyString());
    }

    @Test
    void recoveryPassword_shouldReturnOk_whenValidRequest() throws Exception {
        // Мокируем поведение сервиса
        doNothing().when(authService).recoveryPassword(anyString());

        // Выполнение запроса
        mockMvc.perform(get("/api/v1/auth-server/auth/recovery")
                        .param("code", "recoveryCode"))
                .andExpect(status().isOk());

        // Проверка взаимодействия с сервисом
        verify(authService, times(1)).recoveryPassword(anyString());
    }
}

