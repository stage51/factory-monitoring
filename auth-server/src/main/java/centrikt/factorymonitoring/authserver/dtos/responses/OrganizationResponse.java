package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Schema(description = "Форма вывода организации пользователя")
public class OrganizationResponse extends BaseResponse{
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
