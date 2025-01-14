package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@Schema(description = "Форма вывода информации о выданный refresh токенах")
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    private Long id;
    private String token;
    private String userEmail;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiresAt;
}
