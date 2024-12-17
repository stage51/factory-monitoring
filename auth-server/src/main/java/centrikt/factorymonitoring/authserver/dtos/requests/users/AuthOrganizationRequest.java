package centrikt.factorymonitoring.authserver.dtos.requests.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Форма создания, редактирования организации для пользователя")
@Getter
public class AuthOrganizationRequest {
    private String shortName;
    private String name;
    private String type;
    private String region;
    private String taxpayerNumber;
    private String reasonCode;
    private String address;
    private String specialEmail;
    private String specialPhone;
}
