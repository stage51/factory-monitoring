package centrikt.factorymonitoring.authserver.dtos.requests.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Форма создания, редактирования пользователя для администратора")
public class AdminUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean active;
    private boolean subscribe;
    private String role;
}
