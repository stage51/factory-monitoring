package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Форма ввода refresh токена")
@Data
public class RefreshTokenRequest {
    @NotNull
    private String refreshToken;
}
