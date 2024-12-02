package centrikt.factorymonitoring.authserver.dtos.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class RefreshTokenResponse {
    private Long id;
    private String token;
    private String userEmail;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiresAt;
}
