package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Форма вывода сгенерированного api токена")
@AllArgsConstructor
@NoArgsConstructor
public class ApiTokenResponse {
    private String apiToken;
}
