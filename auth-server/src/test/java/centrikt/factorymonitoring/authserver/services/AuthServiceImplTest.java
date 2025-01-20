package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.models.Recovery;
import centrikt.factorymonitoring.authserver.models.RefreshToken;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.exceptions.InvalidCredentialsException;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.OnlineRepository;
import centrikt.factorymonitoring.authserver.repos.RecoveryRepository;
import centrikt.factorymonitoring.authserver.repos.RefreshTokenRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.impl.AuthServiceImpl;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.iputil.IPUtil;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private IPUtil ipUtil;

    @Mock
    private OnlineRepository onlineRepository;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RecoveryRepository recoveryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTokens_ValidCredentials_ReturnsTokens() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(jwtTokenUtil.getSecretKey()).thenReturn(Keys.hmacShaKeyFor("secret_secret_secret_secret_secret_secret_secret".getBytes()));

        // Act
        AccessRefreshTokenResponse response = authService.createTokens(loginRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(2)).findByEmail(loginRequest.getEmail());
    }

    @Test
    void createTokens_InvalidCredentials_ThrowsInvalidCredentialsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrong-password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.createTokens(loginRequest));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshAccessToken_ValidRefreshToken_ReturnsNewAccessToken() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("valid_refresh_token");
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid_refresh_token");
        refreshToken.setUser(user);
        refreshToken.setIssuedAt(ZonedDateTime.now());
        refreshToken.setExpiresAt(ZonedDateTime.now().plusMinutes(10));

        when(jwtTokenUtil.extractUsername(refreshToken.getToken())).thenReturn("test@example.com");
        when(refreshTokenRepository.findByTokenAndUserEmail("valid_refresh_token", "test@example.com"))
                .thenReturn(Optional.of(refreshToken));
        when(jwtTokenUtil.getSecretKey()).thenReturn(Keys.hmacShaKeyFor("secret_secret_secret_secret_secret_secret_secret".getBytes()));

        // Act
        AccessTokenResponse response = authService.refreshAccessToken(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }

    @Test
    void forgotPassword_ValidEmail_SendsRecoveryEmail() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        authService.forgotPassword(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        verify(recoveryRepository, times(1)).save(any(Recovery.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("emailQueue"), any(EmailMessage.class));
    }

    @Test
    void recoveryPassword_ValidCode_SetsNewPassword() {
        // Arrange
        String recoveryCode = "123456";
        User user = new User();
        user.setEmail("test@example.com");
        Recovery recovery = new Recovery();
        recovery.setCode(recoveryCode);
        recovery.setUser(user);
        recovery.setExpiresAt(ZonedDateTime.now().plusMinutes(10));

        when(recoveryRepository.findByCode(recoveryCode)).thenReturn(Optional.of(recovery));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        authService.recoveryPassword(recoveryCode);

        // Assert
        verify(userRepository, times(1)).save(user);
        verify(recoveryRepository, times(1)).delete(recovery);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("emailQueue"), any(EmailMessage.class));
    }
}
