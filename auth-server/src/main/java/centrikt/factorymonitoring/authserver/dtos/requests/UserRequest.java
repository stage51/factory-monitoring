package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Форма создания, редактирования пользователя")
@Data
@Builder
public class UserRequest {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 8)
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String middleName;
}
