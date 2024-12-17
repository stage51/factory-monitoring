package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Форма логина, пароля")
@Data
public class LoginRequest {
    private String email;
    private String password;
}
