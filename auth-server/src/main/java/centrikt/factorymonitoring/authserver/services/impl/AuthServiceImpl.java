package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.dtos.messages.EmailMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import centrikt.factorymonitoring.authserver.exceptions.*;
import centrikt.factorymonitoring.authserver.exceptions.IllegalArgumentException;
import centrikt.factorymonitoring.authserver.models.*;
import centrikt.factorymonitoring.authserver.repos.*;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.iputil.IPUtil;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.*;

@Service
@RefreshScope
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${security.access-expiration}")
    private long accessTokenExpiration;
    @Value("${security.refresh-expiration}")
    private long refreshTokenExpiration;
    @Value("${user.recovery-url-lifetime}")
    private long recoveryUrlLifetime;

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenUtil jwtTokenUtil;
    private IPUtil ipUtil;
    private OnlineRepository onlineRepository;
    private EntityValidator entityValidator;
    private PasswordEncoder passwordEncoder;
    private RabbitTemplate rabbitTemplate;
    private RecoveryRepository recoveryRepository;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtTokenUtil jwtTokenUtil, IPUtil ipUtil, OnlineRepository onlineRepository, EntityValidator entityValidator, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate, RecoveryRepository recoveryRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.ipUtil = ipUtil;
        this.onlineRepository = onlineRepository;
        this.entityValidator = entityValidator;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.recoveryRepository = recoveryRepository;
        log.info("AuthServiceImpl initialized");
    }

    public AccessRefreshTokenResponse createTokens(LoginRequest loginRequest) {
        log.trace("Entering createTokens method");

        entityValidator.validate(loginRequest);
        log.debug("Validated login request: {}", loginRequest);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", loginRequest.getEmail());
                    return new EntityNotFoundException("User not found with email: " + loginRequest.getEmail());
                });

        if (!user.isActive()){
            log.warn("User is not active: {}", user.getEmail());
            throw new UserNotActiveException("User is not active");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().toString())))
            );
            log.debug("Authentication successful for user: {}", user.getEmail());
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        AccessRefreshTokenResponse response = new AccessRefreshTokenResponse();
        response.setAccessToken(generateAccessToken(user.getEmail(), user.getRole().toString()));
        response.setRefreshToken(createRefreshToken(user.getEmail()).getToken());

        log.info("Tokens created successfully for user: {}", user.getEmail());
        log.trace("Exiting createTokens method");

        return response;
    }

    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        log.trace("Entering refreshAccessToken method");

        entityValidator.validate(refreshTokenRequest);
        log.debug("Validated refresh token request: {}", refreshTokenRequest);

        RefreshToken refreshToken = validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String email = refreshToken.getUser().getEmail();
        String role = refreshToken.getUser().getRole().toString();

        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(generateAccessToken(email, role));

        log.info("Access token refreshed for user: {}", email);
        log.trace("Exiting refreshAccessToken method");

        return response;
    }

    @Transactional
    public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.trace("Entering revokeRefreshToken method");

        entityValidator.validate(refreshTokenRequest);
        log.debug("Validated revoke refresh token request: {}", refreshTokenRequest);

        if (refreshTokenRepository.existsByToken(refreshTokenRequest.getRefreshToken())) {
            refreshTokenRepository.deleteByToken(refreshTokenRequest.getRefreshToken());
            log.info("Refresh token revoked: {}", refreshTokenRequest.getRefreshToken());
        } else {
            log.warn("Refresh token not found for revocation: {}", refreshTokenRequest.getRefreshToken());
            throw new EntityNotFoundException("Refresh token not found with token: " + refreshTokenRequest.getRefreshToken());
        }

        log.trace("Exiting revokeRefreshToken method");
    }

    @Override
    public boolean validateToken(String token) {
        log.trace("Entering validateToken method");

        jwtTokenUtil.validateAndExtractClaims(token);
        log.debug("Token validated successfully");

        log.trace("Exiting validateToken method");
        return true;
    }

    @Override
    public ApiTokenResponse createApiToken(String accessToken, Long expiration) {
        log.trace("Entering createApiToken method");

        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", jwtTokenUtil.extractUsername(accessToken));
                    return new EntityNotFoundException("User not found with email: " + jwtTokenUtil.extractUsername(accessToken));
                });

        ApiTokenResponse response = new ApiTokenResponse();
        response.setApiToken(generateApiToken(user.getEmail(), user.getRole().toString(), expiration));

        log.info("API token created for user: {}", user.getEmail());
        log.trace("Exiting createApiToken method");

        return response;
    }

    @Override
    public void addOnline(String accessToken, HttpServletRequest request) {
        log.trace("Entering addOnline method");

        Online online = new Online();
        online.setEmail(jwtTokenUtil.extractUsername(accessToken));
        online.setIpAddress(ipUtil.getClientIp(request));
        online.setActiveAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        onlineRepository.save(online);

        log.info("User added to online list: {}", online.getEmail());
        log.trace("Exiting addOnline method");
    }

    @Override
    public void forgotPassword(String email) {
        log.trace("Entering forgotPassword method");

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });

        Recovery recovery = new Recovery();
        recovery.setUser(user);
        recovery.setCode(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16));
        recovery.setIssuedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        recovery.setExpiresAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).plusMinutes(recoveryUrlLifetime));
        recoveryRepository.save(recovery);

        log.info("Recovery code generated and sent to user: {}", email);
        try {
            rabbitTemplate.convertAndSend("emailQueue", new EmailMessage(
                    new String[] {email},
                    "Восстановление пароля ЕГАИС Мониторинг",
                    "Восстановление пароля для пользователя: " + email + "\n" +
                            "Чтобы восстановить пароль, вам нужно ввести код, который истечет через " + recoveryUrlLifetime / 60 + " ч: " + recovery.getCode() + "\n" +
                            "Внимание! Если это были не вы, поменяйте пароль"
            ));
        } catch (MessageSendingException e) {
            log.error("Failed to send recovery code message: {}", e.getMessage());
        }

        log.trace("Exiting forgotPassword method");
    }

    @Override
    public void recoveryPassword(String code) {
        log.trace("Entering recoveryPassword method");

        Recovery recovery = recoveryRepository.findByCode(code).orElseThrow(
                () -> {
                    log.error("Recovery not found with code: {}", code);
                    return new EntityNotFoundException("Recovery not found with code: " + code);
                });

        if (!recovery.getCode().equals(code)) {
            log.warn("Recovery code mismatch for code: {}", code);
            throw new IllegalArgumentException("Recovery code does not match");
        }

        if (ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).isAfter(recovery.getExpiresAt())){
            log.warn("Expired recovery code: {}", code);
            throw new ExpiredRecoveryException("Expired recovery code: " + code);
        }

        String tempPassword = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        User user = userRepository.findByEmail(recovery.getUser().getEmail()).orElseThrow(
                () -> {
                    log.error("User not found for recovery with email: {}", recovery.getUser().getEmail());
                    return new EntityNotFoundException("User not found with email: " + recovery.getUser().getEmail());
                });

        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        recoveryRepository.delete(recovery);

        log.info("Password recovered for user: {}", recovery.getUser().getEmail());

        try {
            rabbitTemplate.convertAndSend("emailQueue", new EmailMessage(
                    new String[] {recovery.getUser().getEmail()},
                    "Восстановление пароля ЕГАИС Мониторинг",
                    "Восстановление пароля для пользователя: " + recovery.getUser().getEmail() + "\n" +
                            "Для входа используйте временный пароль: " + tempPassword + "\n" +
                            "Далее измените профиль, указав новый пароль"
            ));
        } catch (MessageSendingException e) {
            log.error("Failed to send temporary password message: {}", e.getMessage());
        }

        log.trace("Exiting recoveryPassword method");
    }

    private String generateAccessToken(String username, String role) {
        log.trace("Entering generateAccessToken method with username: {} and role: {}", username, role);

        Map<String, Object> claims = Map.of("role", role);
        String token = generateToken(claims, username, accessTokenExpiration);

        log.debug("Generated access token for user: {}", username);
        log.trace("Exiting generateAccessToken method");

        return token;
    }

    private String generateRefreshToken(String username) {
        log.trace("Entering generateRefreshToken method with username: {}", username);

        String token = generateToken(Map.of(), username, refreshTokenExpiration);

        log.debug("Generated refresh token for user: {}", username);
        log.trace("Exiting generateRefreshToken method");

        return token;
    }

    private String generateApiToken(String username, String role, Long expiration) {
        log.trace("Entering generateApiToken method with username: {}, role: {}, expiration: {}", username, role, expiration);

        Map<String, Object> claims = Map.of("role", role);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtTokenUtil.getApiKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated API token for user: {}", username);
        log.trace("Exiting generateApiToken method");

        return token;
    }

    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        log.trace("Entering generateToken method with subject: {}, expiration: {}", subject, expiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtTokenUtil.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated token for subject: {}", subject);
        log.trace("Exiting generateToken method");

        return token;
    }

    private RefreshToken createRefreshToken(String userEmail) {
        log.trace("Entering createRefreshToken method with userEmail: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userEmail);
                    return new EntityNotFoundException("User not found with email: " + userEmail);
                });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateRefreshToken(userEmail));
        refreshToken.setIssuedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        refreshToken.setExpiresAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).plusSeconds(refreshTokenExpiration / 1000));

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Created and saved refresh token for user: {}", userEmail);

        log.trace("Exiting createRefreshToken method");

        return refreshToken;
    }

    private RefreshToken validateRefreshToken(String token) {
        log.trace("Entering validateRefreshToken method with token: {}", token);

        String email = jwtTokenUtil.extractUsername(token);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndUserEmail(token, email)
                .filter(rt -> rt.getExpiresAt().isAfter(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue()))))
                .orElseThrow(() -> {
                    log.error("Refresh token not found or expired for token: {}", token);
                    return new EntityNotFoundException("Refresh token not found with token " + token);
                });

        log.debug("Validated refresh token for user: {}", email);
        log.trace("Exiting validateRefreshToken method");

        return refreshToken;
    }
}



