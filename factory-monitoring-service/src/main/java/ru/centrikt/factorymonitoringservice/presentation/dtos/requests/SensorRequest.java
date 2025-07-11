package ru.centrikt.factorymonitoringservice.presentation.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Форма на создание, редактирование сенсора")
public class SensorRequest {
    @Schema(description = "ИНН")
    @NotNull(message = "Taxpayer number must not be null")
    @Length(min = 12, max = 12, message = "Taxpayer number consist of 12 characters")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    @NotNull
    @Pattern(regexp = "\\d+_\\d+", message = "Format must be number_number, for example 12_34")
    private String sensorNumber;
}
