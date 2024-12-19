package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Форма создания, редактирования организации")
@Data
public class OrganizationRequest {
    @NotNull
    private Long userId;
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
