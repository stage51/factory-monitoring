package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.*;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/v1/auth-server/auth")
@Slf4j
public class AuthController implements centrikt.factorymonitoring.authserver.controllers.docs.AuthController {

    private AuthService authService;
    private UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
        log.info("AuthController initialized");
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        log.debug("AuthService set");
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
        log.debug("UserService set");
    }

    @PostMapping("/login")
    public ResponseEntity<AccessRefreshTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("Processing login for user with email: {}", loginRequest.getEmail());
        AccessRefreshTokenResponse response = authService.createTokens(loginRequest);
        log.debug("Login successful for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Refreshing access token for user with refresh token: {}", request.getRefreshToken());
        AccessTokenResponse response = authService.refreshAccessToken(request);
        log.debug("Access token successfully refreshed for user.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        log.info("Registering new user with email: {}", userRequest.getEmail());
        UserResponse response = userService.create(userRequest);
        log.debug("User registered successfully with email: {}", userRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        log.info("Revoking refresh token: {}", request.getRefreshToken());
        authService.revokeRefreshToken(request);
        log.debug("User logged out successfully, refresh token revoked.");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkAuth(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        log.info("Validating token: {}", accessToken);
        authService.validateToken(accessToken);
        authService.addOnline(accessToken, request);
        log.debug("Token validation successful for access token: {}", accessToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/create-api-token")
    public ResponseEntity<ApiTokenResponse> createApiToken(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("expiration") Long expiration) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Creating API token for user with access token: {}", accessToken);
        ApiTokenResponse response = authService.createApiToken(accessToken, expiration);
        log.debug("API token created successfully for user.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(@RequestParam("email") String email) {
        log.info("Sending password recovery email for user: {}", email);
        authService.forgotPassword(email);
        log.debug("Password recovery email sent to: {}", email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recovery")
    public ResponseEntity<Void> recoveryPassword(@RequestParam("code") String code) {
        log.info("Processing password recovery for code: {}", code);
        authService.recoveryPassword(code);
        log.debug("Password recovery successful for code: {}", code);
        return ResponseEntity.ok().build();
    }
}

