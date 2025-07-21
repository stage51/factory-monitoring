package ru.centrikt.factorymonitoringservice.presentation.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Форма на создание, редактирование сенсора")
public class SensorRequest {
    @Schema(description = "Название организации")
    @NotNull(message = "Organization name must not be null")
    @Length(min = 6, max = 50, message = "Organization name must be more than 6 and less than 50 characters")
    private String organizationName;

    @Schema(description = "ИНН")
    @NotNull(message = "Taxpayer number must not be null")
    @Length(min = 12, max = 12, message = "Taxpayer number consist of 12 characters")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    @NotNull
    @Pattern(regexp = "\\d+_\\d+", message = "Format must be number_number, for example 12_34")
    private String sensorNumber;
}
