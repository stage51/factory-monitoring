package ru.centrikt.transportmonitoringservice.presentation.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Форма на создание, редактирование сенсора")
public class SensorRequest {
    @Schema(description = "Название организации")
    @NotNull(message = "Organization name must not be null")
    @Length(min = 6, max = 50, message = "Organization name must be more than 6 and less than 50 characters")
    private String organizationName;

    @Schema(description = "Гос номер")
    @NotNull(message = "Gov number must not be null")
    @Length(min = 4, max = 20, message = "Gov number must be more than 4 and less than 20 characters")
    private String govNumber;

    @Schema(description = "ИНН")
    @NotNull(message = "Taxpayer number must not be null")
    @Length(min = 12, max = 12, message = "Taxpayer number consist of 12 characters")
    private String taxpayerNumber;

    @Schema(description = "GUID")
    @NotNull(message = "GUID must not be null")
    private String guid;
}
