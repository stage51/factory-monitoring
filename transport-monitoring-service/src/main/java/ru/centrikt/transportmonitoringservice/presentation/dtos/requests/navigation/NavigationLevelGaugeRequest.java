package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Форма на создание, редактирование уровнемеров")
public class NavigationLevelGaugeRequest {
    @Schema(description = "Номер")
    @NotNull(message = "Number must not be null")
    private Long number;
    @Schema(description = "Уровень")
    @NotNull(message = "Readings must not be null")
    @Digits(integer = 12, fraction = 3, message = "Readings must be a valid decimal number with up to 12 digits and 3 decimal places")
    private BigDecimal readings;
    @Schema(description = "Температура")
    @NotNull(message = "Temperature must not be null")
    @Digits(integer = 3, fraction = 2, message = "Temperature must be a valid decimal number with up to 3 digits and 2 decimal places")
    private BigDecimal temperature;
    @Schema(description = "Плотность")
    @NotNull(message = "Density must not be null")
    @Digits(integer = 0, fraction = 3, message = "Density must be a valid decimal number with up to 0 digits and 3 decimal places")
    private BigDecimal density;
}
