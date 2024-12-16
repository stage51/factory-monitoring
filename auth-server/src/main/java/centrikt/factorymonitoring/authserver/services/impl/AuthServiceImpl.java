package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.exceptions.IllegalArgumentException;
import centrikt.factorymonitoring.authserver.exceptions.InvalidCredentialsException;
import centrikt.factorymonitoring.authserver.exceptions.UserNotActiveException;
import centrikt.factorymonitoring.authserver.mappers.OrganizationMapper;
import centrikt.factorymonitoring.authserver.mappers.UserMapper;
import centrikt.factorymonitoring.authserver.models.Online;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.RefreshToken;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.OnlineRepository;
import centrikt.factorymonitoring.authserver.repos.OrganizationRepository;
import centrikt.factorymonitoring.authserver.repos.RefreshTokenRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.utils.entityvalidator.EntityValidator;
import centrikt.factorymonitoring.authserver.utils.iputil.IPUtil;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RefreshScope
public class AuthServiceImpl implements AuthService {
    @Value("${security.access-expiration}")
    private long accessTokenExpiration;
    @Value("${security.refresh-expiration}")
    private long refreshTokenExpiration;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenUtil jwtTokenUtil;
    private IPUtil ipUtil;
    private OnlineRepository onlineRepository;

    public AuthServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            IPUtil ipUtil,
            OnlineRepository onlineRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.onlineRepository = onlineRepository;
        this.ipUtil = ipUtil;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Autowired
    public void setIpUtil(IPUtil ipUtil) {
        this.ipUtil = ipUtil;
    }
    @Autowired
    public void setOnlineRepository(OnlineRepository onlineRepository) {
        this.onlineRepository = onlineRepository;
    }

    public AccessRefreshTokenResponse createTokens(LoginRequest loginRequest) {
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
        RefreshToken refreshToken = validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String email = refreshToken.getUser().getEmail();
        String role = refreshToken.getUser().getRole().toString();

        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(generateAccessToken(email, role));
        return response;
    }

    @Transactional
    public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
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
}



