package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;


public interface AuthService {
    AccessRefreshTokenResponse createTokens(LoginRequest loginRequest);
    AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);
    UserResponse getProfile(String accessToken);
    UserResponse updateProfile(String accessToken, UserRequest userRequest);
    void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest);
    OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    void deleteOrganization(String accessToken);
}
