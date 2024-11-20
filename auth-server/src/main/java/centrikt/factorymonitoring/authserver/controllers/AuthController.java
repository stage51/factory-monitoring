package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.requests.AccessTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.services.AuthService;
import centrikt.factorymonitoring.authserver.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/profile")
    public ResponseEntity<UserResponse> profile(@RequestBody AccessTokenRequest accessTokenRequest) {
        return ResponseEntity.ok(authService.getProfile(accessTokenRequest));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.revokeRefreshToken(request);
        return ResponseEntity.noContent().build();
    }
}
