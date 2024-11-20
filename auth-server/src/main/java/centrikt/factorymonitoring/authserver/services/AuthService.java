package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.AccessTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;


public interface AuthService {
    AccessRefreshTokenResponse createTokens(LoginRequest loginRequest);
    AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
    UserResponse getProfile(AccessTokenRequest accessTokenRequest);
    void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest);
}
