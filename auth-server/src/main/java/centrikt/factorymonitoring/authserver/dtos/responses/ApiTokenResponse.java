package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Форма вывода сгенерированного api токена")
public class ApiTokenResponse {
    private String apiToken;
}
