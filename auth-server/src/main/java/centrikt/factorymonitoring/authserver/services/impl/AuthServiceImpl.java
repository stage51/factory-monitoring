package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.exceptions.UserNotActiveException;
import centrikt.factorymonitoring.authserver.mappers.OrganizationMapper;
import centrikt.factorymonitoring.authserver.mappers.UserMapper;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.RefreshToken;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.OrganizationRepository;
import centrikt.factorymonitoring.authserver.repos.RefreshTokenRepository;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import java.util.Date;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenUtil jwtTokenUtil;
    private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.access-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.passwordEncoder = passwordEncoder;
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
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AccessRefreshTokenResponse createTokens(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + loginRequest.getEmail()));
        if (!user.isActive()){
            throw new UserNotActiveException("User is not active");
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

    public UserResponse getProfile(String accessToken) {
        String username = jwtTokenUtil.extractUsername(accessToken);
        return UserMapper.toResponse(
                userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username))
        );
    }

    @Override
    public UserResponse updateProfile(String accessToken, UserRequest userRequest) {
        String username = jwtTokenUtil.extractUsername(accessToken);
        User existingUser = userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        existingUser = UserMapper.toEntityFromUpdateRequest(existingUser, userRequest);
        existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        return UserMapper.toResponse(userRepository.save(existingUser));
    }

    @Transactional
    public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        if (refreshTokenRepository.existsByToken(refreshTokenRequest.getRefreshToken())) {
            refreshTokenRepository.deleteByToken(refreshTokenRequest.getRefreshToken());
        } else throw new EntityNotFoundException("Refresh token not found with token: " + refreshTokenRequest.getRefreshToken());
    }

    @Override
    public OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));

        Organization organization = OrganizationMapper.toEntityFromCreateRequest(organizationRequest, user);
        user.setOrganization(organization);
        organizationRepository.save(organization);
        userRepository.save(user);

        return OrganizationMapper.toResponse(organization);
    }

    @Override
    public OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));

        Organization existingOrganization = organizationRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Organization for user " + user.getEmail() + " not found"));

        Organization organization = OrganizationMapper.toEntityFromUpdateRequest(existingOrganization, organizationRequest);
        organizationRepository.save(organization);

        return OrganizationMapper.toResponse(organization);
    }

    @Override
    @Transactional
    public void deleteOrganization(String accessToken) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractUsername(accessToken))
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + jwtTokenUtil.extractUsername(accessToken) + " not found"));

        organizationRepository.deleteByUser(user);
    }

    private String generateAccessToken(String username, String role) {
        Map<String, Object> claims = Map.of("role", role);
        return generateToken(claims, username, accessTokenExpiration);
    }

    private String generateRefreshToken(String username) {
        return generateToken(Map.of(), username, refreshTokenExpiration);
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
        refreshToken.setIssuedAt(ZonedDateTime.now());
        refreshToken.setExpiresAt(ZonedDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken validateRefreshToken(String token) {
        String email = jwtTokenUtil.extractUsername(token);
        return refreshTokenRepository.findByTokenAndUserEmail(token, email)
                .filter(rt -> rt.getExpiresAt().isAfter(ZonedDateTime.now()))
                .orElseThrow(() -> new EntityNotFoundException("Refresh token not found with token " + token));
    }
}



