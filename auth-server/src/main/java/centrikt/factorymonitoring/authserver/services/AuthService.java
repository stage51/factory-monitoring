package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.*;
import jakarta.servlet.http.HttpServletRequest;


public interface AuthService {
    AccessRefreshTokenResponse createTokens(LoginRequest loginRequest);
    AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
    void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest);
    boolean validateToken(String token);
    ApiTokenResponse createApiToken(String token, Long expiration);
    void addOnline(String accessToken, HttpServletRequest request);
    void forgotPassword(String email);
    void recoveryPassword(String code);
}
