package centrikt.factorymonitoring.authserver.dtos.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;


@Getter
@SuperBuilder
@Schema(description = "Форма вывода пользователя")
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends BaseResponse{
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private boolean active;
    private String role;
    private OrganizationResponse organization;
    private SettingResponse setting;
}
