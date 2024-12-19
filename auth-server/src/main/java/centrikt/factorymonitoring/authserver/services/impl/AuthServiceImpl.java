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
    }

    public AccessRefreshTokenResponse createTokens(LoginRequest loginRequest) {
        entityValidator.validate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + loginRequest.getEmail()));
        if (!user.isActive()){
            throw new UserNotActiveException("User is not active");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().toString())))
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        AccessRefreshTokenResponse response = new AccessRefreshTokenResponse();
        response.setAccessToken(generateAccessToken(user.getEmail(), user.getRole().toString()));
        response.setRefreshToken(createRefreshToken(user.getEmail()).getToken());
        return response;
    }

    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        entityValidator.validate(refreshTokenRequest);
        RefreshToken refreshToken = validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String email = refreshToken.getUser().getEmail();
        String role = refreshToken.getUser().getRole().toString();

        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(generateAccessToken(email, role));
        return response;
    }

    @Transactional
    public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        entityValidator.validate(refreshTokenRequest);
        if (refreshTokenRepository.existsByToken(refreshTokenRequest.getRefreshToken())) {
            refreshTokenRepository.deleteByToken(refreshTokenRequest.getRefreshToken());
        } else throw new EntityNotFoundException("Refresh token not found with token: " + refreshTokenRequest.getRefreshToken());
    }

    @Override
    public boolean validateToken(String token) {
        jwtTokenUtil.validateAndExtractClaims(token);
        return true;
    }

    @Override
    public ApiTokenResponse createApiToken(String accessToken, Long expiration) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + jwtTokenUtil.extractUsername(accessToken)));
        ApiTokenResponse response = new ApiTokenResponse();
        response.setApiToken(generateApiToken(user.getEmail(), user.getRole().toString(), expiration));
        return response;
    }

    @Override
    public void addOnline(String accessToken, HttpServletRequest request) {
        Online online = new Online();
        online.setEmail(jwtTokenUtil.extractUsername(accessToken));
        online.setIpAddress(ipUtil.getClientIp(request));
        online.setActiveAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        onlineRepository.save(online);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found with email: " + email));
        Recovery recovery = new Recovery();
        recovery.setUser(user);
        recovery.setCode(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 6));
        recovery.setIssuedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        recovery.setExpiresAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).plusMinutes(recoveryUrlLifetime));
        recoveryRepository.save(recovery);
        rabbitTemplate.convertAndSend("emailQueue", new EmailMessage(
                new String[] {email},
                "Восстановление пароля ЕГАИС Мониторинг",
                "Восстановление пароля для пользователя: " + email + "\n" +
                "Чтобы восстановить пароль, вам нужно ввести код, который истечет через " + recoveryUrlLifetime / 60 + " ч: " + recovery.getCode() + "\n" +
                "Внимание! Если это были не вы, поменяйте пароль"
        ));
    }

    @Override
    public void recoveryPassword(String code) {
        Recovery recovery = recoveryRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException("Recovery not found with code: " + code));
        if (!recovery.getCode().equals(code)) {
            throw new IllegalArgumentException("Recovery code does not match");
        }
        if (ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).isAfter(recovery.getExpiresAt())){
            throw new ExpiredRecoveryException("Expired recovery code: " + code);
        }
        String tempPassword = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase().substring(0, 16);
        User user = userRepository.findByEmail(recovery.getUser().getEmail()).orElseThrow(
                () -> new EntityNotFoundException("User not found with email: " + recovery.getUser().getEmail()));
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        recoveryRepository.delete(recovery);
        rabbitTemplate.convertAndSend("emailQueue", new EmailMessage(
                new String[] {recovery.getUser().getEmail()},
                "Восстановление пароля ЕГАИС Мониторинг",
                "Восстановление пароля для пользователя: " + recovery.getUser().getEmail() + "\n" +
                        "Для входа используйте временный пароль: " + tempPassword + "\n" +
                        "Далее измените профиль, указав новый пароль"
        ));
    }

    private String generateAccessToken(String username, String role) {
        Map<String, Object> claims = Map.of("role", role);
        return generateToken(claims, username, accessTokenExpiration);
    }

    private String generateRefreshToken(String username) {
        return generateToken(Map.of(), username, refreshTokenExpiration);
    }

    private String generateApiToken(String username, String role, Long expiration) {
        Map<String, Object> claims = Map.of("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiration))
                .signWith(jwtTokenUtil.getApiKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtTokenUtil.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private RefreshToken createRefreshToken(String userEmail) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail)));
        refreshToken.setToken(generateRefreshToken(userEmail));
        refreshToken.setIssuedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        refreshToken.setExpiresAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())).plusSeconds(refreshTokenExpiration / 1000));
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken validateRefreshToken(String token) {
        String email = jwtTokenUtil.extractUsername(token);
        return refreshTokenRepository.findByTokenAndUserEmail(token, email)
                .filter(rt -> rt.getExpiresAt().isAfter(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue()))))
                .orElseThrow(() -> new EntityNotFoundException("Refresh token not found with token " + token));
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }
}



