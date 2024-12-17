package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Форма создания, редактирования пользователя")
@Getter
public class UserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean subscribe;
}
