package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Форма вывода нового access токена после истечения старого")
public class AccessTokenResponse {
    private String accessToken;
}
