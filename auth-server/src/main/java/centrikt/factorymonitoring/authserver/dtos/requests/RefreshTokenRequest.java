package centrikt.factorymonitoring.authserver.dtos.requests;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
