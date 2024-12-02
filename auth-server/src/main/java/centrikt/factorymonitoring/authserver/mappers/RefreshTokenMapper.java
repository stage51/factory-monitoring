package centrikt.factorymonitoring.authserver.mappers;


import centrikt.factorymonitoring.authserver.dtos.responses.RefreshTokenResponse;
import centrikt.factorymonitoring.authserver.models.RefreshToken;

public class RefreshTokenMapper {
    public static RefreshTokenResponse toResponse(RefreshToken refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        RefreshTokenResponse dto = RefreshTokenResponse.builder().id(refreshToken.getId()).token(refreshToken.getToken())
                .expiresAt(refreshToken.getExpiresAt()).issuedAt(refreshToken.getIssuedAt()).build();
        return dto;
    }
}
