package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.*;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth-server/auth")
public class AuthController {

    private AuthService authService;
    private UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }
    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AccessRefreshTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.createTokens(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.create(userRequest));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.revokeRefreshToken(request);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/check")
    public ResponseEntity<Void> checkAuth(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        authService.validateToken(accessToken);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/create-api-token")
    public ResponseEntity<ApiTokenResponse> createApiToken(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(
                authService.createApiToken(authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader)
        );
    }
}
