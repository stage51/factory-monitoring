package centrikt.factorymonitoring.authserver.dtos.requests.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "Форма создания, редактирования организации для пользователя")
@Getter
public class AuthOrganizationRequest {
    @NotNull
    private String shortName;
    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private String region;
    @NotNull
    private String taxpayerNumber;
    @NotNull
    private String reasonCode;
    @NotNull
    private String address;
    @NotNull
    private String specialEmail;
    @NotNull
    private String specialPhone;
}
