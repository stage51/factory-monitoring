package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Форма вывода access и refresh токена после авторизации")
public class AccessRefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
