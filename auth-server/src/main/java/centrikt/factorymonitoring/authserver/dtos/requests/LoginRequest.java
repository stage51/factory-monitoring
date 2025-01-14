package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Форма логина, пароля")
@Data
@AllArgsConstructor
public class LoginRequest {
    @Email
    private String email;
    @Size(min = 8)
    private String password;
}
