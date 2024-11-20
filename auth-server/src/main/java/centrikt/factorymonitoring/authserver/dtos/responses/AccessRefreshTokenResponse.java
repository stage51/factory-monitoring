package centrikt.factorymonitoring.authserver.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessRefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
